package dev.woos.toons_api.usecase

import dev.woos.toons_api.api.HomeDto
import dev.woos.toons_api.api.dto.WebtoonDto
import dev.woos.toons_api.domain.alarm.AlarmRepository
import dev.woos.toons_api.domain.board.PostRepository
import dev.woos.toons_api.domain.webtoon.WebtoonRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class QueryService(
    private val webtoonRepository: WebtoonRepository,
    private val postRepository: PostRepository,
    private val alarmRepository: AlarmRepository,

    ) {
    suspend fun queryHome(): HomeDto = coroutineScope {
        val postsDeferred =
            async { postRepository.findTop5ByCreatedAtAfterOrderByLikeCountDesc(LocalDateTime.now().minusWeeks(1)) }
        val completedWebtoonsDeferred = async { webtoonRepository.findTop10ByCompletedTrueOrderByUpdatedAtDesc() }

        val alarmsDeferred = async { alarmRepository.findTop5MostAlarmRegisteredWebtoons() }

        val hostPosts = postsDeferred.await()
        val completedWebtoons = completedWebtoonsDeferred.await()
        val topAlarmWebtoonsIds = alarmsDeferred.await().map { it.webtoonId }
        val topAlarmWebtoons = webtoonRepository.findAllById(topAlarmWebtoonsIds)



        HomeDto(
            hotPosts = hostPosts.map {
                HomeDto.HostPostDto(
                    id = it.id,
                    title = it.title,
                )
            }.toList(),
            completedWebtoons = completedWebtoons.map {
                WebtoonDto(
                    id = it.id,
                    title = it.title,
                    thumbnailUrl = it.thumbnailUrl,
                    platform = it.platform,
                    dayOfWeek = it.dayOfWeek,
                    link = it.link,
                )
            }.toList(),
            topAlarmWebtoons = topAlarmWebtoons.map {
                WebtoonDto(
                    id = it.id,
                    title = it.title,
                    thumbnailUrl = it.thumbnailUrl,
                    platform = it.platform,
                    dayOfWeek = it.dayOfWeek,
                    link = it.link,
                )
            }.toList()
        )
    }

}