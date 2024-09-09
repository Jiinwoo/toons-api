package dev.woos.toons_api.infra

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.jsoup.Jsoup
import org.springframework.stereotype.Component

enum class NaverWeek {
    MON, TUE, WED, THU, FRI, SAT, SUN
}

@Component
class JsoupCrawlerImpl(

) {
    private val logger = KotlinLogging.logger { }
//    private val weekDays = []

    suspend fun getNaverWebNovelList(): Result<List<String>> = runCatching {
        val documents = coroutineScope {
            val documents = NaverWeek.values().map { weekDay ->
                async(Dispatchers.IO) {
                    Jsoup.connect("https://novel.naver.com/webnovel/weekdayList?week=${weekDay}&genre=all&order=Read")
                        .get()
                }
            }
            documents.awaitAll()
        }

        documents.flatMap { document ->
            val anchorItems = document.select("#content > div > ul > li > a")
            anchorItems.map { anchor ->
                val href = anchor.attr("href")
                val title = anchor.attr("title")
                logger.info { "title = $title" }
                title
            }
        }
    }
}
