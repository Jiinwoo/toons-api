package dev.woos.toons_api.config.quartz

import dev.woos.toons_api.usecase.WebtoonService
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.quartz.JobExecutionContext
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component

@Component
class CrawlerJob(
    private val webtoonService: WebtoonService,
) : QuartzJobBean() {
    private val logger = KotlinLogging.logger { }
    override fun executeInternal(context: JobExecutionContext) {
        logger.info { "CrawlerJob started" }
        runBlocking {
            try {
                val naverAsync = async { webtoonService.crawlerNAVER() }
                val kakaoAsync = async { webtoonService.crawlerKAKAO() }
                naverAsync.await()
                kakaoAsync.await()
                val naverCompleteAsync = async { webtoonService.crawlerCompletedNAVER() }
                val kakaoCompleteAsync = async { webtoonService.crawlerCompletedKAKAO() }
                naverCompleteAsync.await()
                kakaoCompleteAsync.await()
            } catch (e: Exception) {
                logger.error(e) { "Error in CrawlerJob" }
            }
        }
        logger.info { "CrawlerJob ended" }
    }


}