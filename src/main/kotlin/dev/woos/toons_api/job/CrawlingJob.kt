package dev.woos.toons_api.job

import org.quartz.JobExecutionContext
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component

@Component
class CrawlingJob : QuartzJobBean() {
    override fun executeInternal(context: JobExecutionContext) {

    }
}