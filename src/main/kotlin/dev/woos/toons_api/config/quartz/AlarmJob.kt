package dev.woos.toons_api.config.quartz

import dev.woos.toons_api.usecase.WebtoonService
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.quartz.JobExecutionContext
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component

@Component
class AlarmJob(
    private val webtoonService: WebtoonService
) : QuartzJobBean() {
    private val logger = KotlinLogging.logger { }

    override fun executeInternal(context: JobExecutionContext) {
        logger.info { "AlarmJob started" }
        runBlocking {
            try {
                webtoonService.sendEndedWebtoonAlert()
            } catch (e: Exception) {
                logger.error(e) { "Error in AlarmJob" }
            }
        }
        logger.info { "AlarmJob ended" }
    }
}