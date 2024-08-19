package dev.woos.toons_api.usecase

import dev.woos.toons_api.api.dto.WebtoonDto
import dev.woos.toons_api.domain.webtoon.WebtoonCrawler
import dev.woos.toons_api.domain.webtoon.WebtoonRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class WebtoonService(
    private val webtoonRepository: WebtoonRepository,
    private val webtoonCrawler: WebtoonCrawler,
) {
    private val logger = KotlinLogging.logger {}
    suspend fun crawlerNAVER() {
        val crawlerResult = webtoonCrawler.getNaverWebtoonList().getOrThrow()
        val ids = webtoonRepository.upsertBatch(crawlerResult)
        logger.info { "upserted webtoons: $ids" }
    }

    suspend fun crawlerCompletedNAVER() {
        val crawlerResult = webtoonCrawler.getNaverCompletedWebtoonTitles().getOrThrow()
        logger.info { "crawlerResult : ${crawlerResult}" }
        val platformIds = crawlerResult.map { it.first }
        webtoonRepository.findAllByIdIn(platformIds).map {
            it.isCompleted()
            it
        }.also { webtoonRepository.saveAll(it) }
    }

    suspend fun crawlerKAKAO() = coroutineScope {
        val generalWeekdaysResponse = async { webtoonCrawler.getKakaoGeneralWeekdaysWebtoonList().getOrThrow() }
        val novelWeekdaysResponse = async { webtoonCrawler.getKakaoNovelWeekdaysWebtoonList().getOrThrow() }
        val crawlerResult = generalWeekdaysResponse.await() + novelWeekdaysResponse.await()
        val ids = webtoonRepository.upsertBatch(crawlerResult)
        logger.info { "upserted webtoons: $ids" }
    }

    suspend fun crawlerCompletedKAKAO() {
        val crawlerResult = webtoonCrawler.getKakaoCompletedWebtoonTitles().getOrThrow()
        logger.info { "crawlerResult : ${crawlerResult}" }
        val platformIds = crawlerResult.map { it.first }
        webtoonRepository.findAllByIdIn(platformIds).map {
            it.isCompleted()
            it
        }.also { webtoonRepository.saveAll(it) }
    }

    @Transactional(readOnly = true)
    suspend fun findAllWithFilters(
        pageable: Pageable, title: String?, days: List<String>?, platforms: List<String>?
    ): Page<WebtoonDto> = coroutineScope {
        val webtoonsDeferred = async {
            webtoonRepository.findAllWithFilters(pageable, title, days, platforms).toList()
        }
        val countDeferred = async {
            webtoonRepository.countWithFilters(title, days, platforms)
        }

        val webtoonList = webtoonsDeferred.await()
        val count = countDeferred.await()

        PageImpl(
            webtoonList.map {
                WebtoonDto(
                    id = it.id,
                    title = it.title,
                    thumbnailUrl = it.thumbnailUrl,
                    platform = it.platform,
                    dayOfWeek = it.dayOfWeek,
                    link = it.link
                )
            }.toList(), pageable, count
        )
    }


}
