package dev.woos.toons_api.usecase

import dev.woos.toons_api.api.dto.AlarmDto
import dev.woos.toons_api.api.dto.WebtoonDto
import dev.woos.toons_api.domain.alarm.Alarm
import dev.woos.toons_api.domain.alarm.AlarmRepository
import dev.woos.toons_api.domain.webtoon.WebtoonRepository
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AlarmService(
    private val alarmRepository: AlarmRepository,
    private val webtoonRepository: WebtoonRepository,
) {
    @Transactional(readOnly = true)
    suspend fun getAlarmList(username: String): List<AlarmDto> {
        val alarmList = alarmRepository.findAllByMemberId(username.toLong())
            .toList()

        val webtoonIds = alarmList.map { it.webtoonId }.distinct()

        val webtoonMap = webtoonRepository.findAllByIdIn(webtoonIds)
            .toList()
            .associateBy { it.id }

        return alarmList.map {
            val webtoon = webtoonMap[it.webtoonId]!!
            AlarmDto(
                id = it.id,
                webtoon = WebtoonDto(
                    id = webtoon.id,
                    title = webtoon.title,
                    thumbnailUrl = webtoon.thumbnailUrl,
                    platform = webtoon.platform,
                    dayOfWeek = webtoon.dayOfWeek,
                    link = webtoon.link,
                ),
            )
        }

    }

    @Transactional
    suspend fun createAlarm(username: String, webtoonId: Long): Long {
        return alarmRepository.save(
            Alarm(
                webtoonId = webtoonId,
                memberId = username.toLong(),
            )
        ).id
    }

    suspend fun deleteAlarm(username: String, id: Long) {
        alarmRepository.deleteByIdAndMemberId(id, username.toLong())
    }
}