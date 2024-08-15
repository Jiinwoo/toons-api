package dev.woos.toons_api.usecase

import dev.woos.toons_api.api.dto.WebtoonDto
import dev.woos.toons_api.domain.webtoon.NAVERCrawler
import dev.woos.toons_api.domain.webtoon.WebtoonRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitBodyOrNull

@Transactional
@Service
class WebtoonService(
    private val webtoonRepository: WebtoonRepository,
    private val naverCrawler: NAVERCrawler,
) {
    private val logger = KotlinLogging.logger {}
    suspend fun crawerNAVER(): String {
        val crawlerResult = naverCrawler.getWebtoonList()
            .getOrThrow()
        print(crawlerResult.size)
        val ids = webtoonRepository.upsertBatch(crawlerResult)
        logger.info { "upserted webtoons: $ids" }
        return ""
    }

    suspend fun findAllWithPage(pageable: Pageable): Page<WebtoonDto> {
        val total = webtoonRepository.count()
        val webtoonList = webtoonRepository.findAllBy(pageable)
        return PageImpl(
            webtoonList.map {
                WebtoonDto(
                    id = it.id,
                    title = it.title,
                    thumbnailUrl = it.thumbnailUrl,
                    platform = it.platform,
                    dayOfWeek = it.dayOfWeek
                )
            }.toList(),
            pageable,
            total
        )
    }

}