package dev.woos.toons_api.config

import dev.woos.toons_api.config.quartz.CrawlerJob
import org.quartz.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QuartzConfig {

    @Bean
    fun crawlerJobDetail(): JobDetail {
        return JobBuilder.newJob()
            .ofType(CrawlerJob::class.java)
            .storeDurably()
            .withIdentity("CrawlerJob")
            .withDescription("CrawlerJob")
            .build()
    }

    @Bean
    fun crawlerJobTrigger(): Trigger {
        return TriggerBuilder.newTrigger()
            .forJob(crawlerJobDetail())
            .withIdentity("CrawlerJobTrigger")
            .withDescription("CrawlerJobTrigger")
//            .withSchedule(
//                SimpleScheduleBuilder.simpleSchedule()
//                    .withRepeatCount(0)
//                    .withIntervalInSeconds(10)
//            )
            .withSchedule(
                CronScheduleBuilder.cronSchedule("0 0 0 * * ?").inTimeZone(
                    java.util.TimeZone.getTimeZone("Asia/Seoul")
                )
            )
            .build()
    }
}