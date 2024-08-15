package dev.woos.toons_api.infra

import dev.woos.toons_api.domain.common.Platform
import dev.woos.toons_api.domain.webtoon.NAVERCrawler
import dev.woos.toons_api.domain.webtoon.Webtoon
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.DayOfWeek

data class NaverDto(
    val dayOfWeek: DayOfWeek,
    val titleListMap: Map<String, List<NaverWebtoon>>
) {
    data class NaverWebtoon(
        val author: String,
        val thumbnailUrl: String,
        val titleName: String,
        val titleId: Long,
        val viewCount: Long,
    )
}

@Component
class CrawlerImpl(
    private val webClient: WebClient,
) : NAVERCrawler {
    override suspend fun getWebtoonList(): Result<List<Webtoon>> = runCatching {
        val response = webClient.get()
            .uri("https://comic.naver.com/api/webtoon/titlelist/weekday?order=user")
            .retrieve()
            .awaitBody<NaverDto>()

        response.titleListMap.map { (dayOfWeek, webtoons) ->
            webtoons.map { webtoon ->
                Webtoon(
                    title = webtoon.titleName,
                    thumbnailUrl = webtoon.thumbnailUrl,
                    dayOfWeek = DayOfWeek.valueOf(dayOfWeek.uppercase()),
                    link = "https://comic.naver.com/webtoon/list?titleId=${webtoon.titleId}",
                    platform = Platform.NAVER,
                    platformId = webtoon.titleId,
                )
            }
        }.flatten()
    }

    data class FinishedNaverWebtoonDto(
        val pageInfo: Any,
        val titleList: List<Title>
    ) {
        data class Title(
            val titleId: Long,
            val title: String,
        )
    }

    override suspend fun getCompletedWebtoonTitles(): Result<List<Pair<Long, String>>> = runCatching {
        val response = webClient.get()
            .uri("https://comic.naver.com/api/webtoon/titlelist/finished?page=1&order=UPDATE")
            .retrieve()
            .awaitBody<FinishedNaverWebtoonDto>()
        response.titleList.map { it.titleId to it.title }
    }


}