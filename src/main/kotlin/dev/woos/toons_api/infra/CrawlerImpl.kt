package dev.woos.toons_api.infra

import dev.woos.toons_api.domain.common.Platform
import dev.woos.toons_api.domain.webtoon.Webtoon
import dev.woos.toons_api.domain.webtoon.WebtoonCrawler
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

data class KAKAOWebtoonDTO(
    val data: Data
) {
    data class Data constructor(
        val sections: List<Section>
    ) {
        data class Section internal constructor(
            val cardGroups: List<CardGroup>,
            val title: String
        ) {
            data class CardGroup internal constructor(
                val cards: List<Card>
            ) {
                data class Card internal constructor(
                    val content: Content
                ) {
                    data class Content internal constructor(
                        val title: String,
                        val featuredCharacterImageB: String,
                        val id: Long
                    )
                }
            }
        }
    }
}


@Component
class CrawlerImpl(
    private val webClient: WebClient,
) : WebtoonCrawler {
    override suspend fun getNaverWebtoonList(): Result<List<Webtoon>> = runCatching {
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
            val titleName: String,
        )
    }

    override suspend fun getNaverCompletedWebtoonTitles(): Result<List<Pair<Long, String>>> = runCatching {
        val response = webClient.get()
            .uri("https://comic.naver.com/api/webtoon/titlelist/finished?page=1&order=UPDATE")
            .retrieve()
            .awaitBody<FinishedNaverWebtoonDto>()
        response.titleList.map { it.titleId to it.titleName }
    }

    override suspend fun getKakaoGeneralWeekdaysWebtoonList(): Result<List<Webtoon>> = runCatching {
        val response = webClient.get()
            .uri("https://gateway-kw.kakao.com/section/v2/pages/general-weekdays")
            .retrieve()
            .awaitBody<KAKAOWebtoonDTO>()
        response.data.sections.flatMap { section ->
            section.cardGroups.flatMap { cardGroup ->
                cardGroup.cards.map { card ->
                    Webtoon(
                        title = card.content.title,
                        thumbnailUrl = "${card.content.featuredCharacterImageB}.png",
                        dayOfWeek = convertToDayOfWeek(section.title),
                        link = "https://webtoon.kakao.com/content/${card.content.title}/${card.content.id}",
                        platform = Platform.KAKAO,
                        platformId = card.content.id,
                    )
                }
            }
        }
    }

    override suspend fun getKakaoNovelWeekdaysWebtoonList(): Result<List<Webtoon>> = runCatching {
        val response = webClient.get()
            .uri("https://gateway-kw.kakao.com/section/v2/pages/novel-weekdays")
            .retrieve()
            .awaitBody<KAKAOWebtoonDTO>()
        response.data.sections.flatMap { section ->
            section.cardGroups.flatMap { cardGroup ->
                cardGroup.cards.map { card ->
                    Webtoon(
                        title = card.content.title,
                        thumbnailUrl = "${card.content.featuredCharacterImageB}.png",
                        dayOfWeek = convertToDayOfWeek(section.title),
                        link = "https://webtoon.kakao.com/content/${card.content.title}/${card.content.id}",
                        platform = Platform.KAKAO,
                        platformId = card.content.id,
                    )
                }
            }
        }
    }

    data class KAKAOCompleteWebtoonDTO(
        val data: List<Data>
    ) {
        data class Data(
            val cardGroups: List<KAKAOWebtoonDTO.Data.Section.CardGroup>
        )
    }

    override suspend fun getKakaoCompletedWebtoonTitles(): Result<List<Pair<Long, String>>> = runCatching {
        val response = webClient.get()
            .uri("https://gateway-kw.kakao.com/section/v2/sections?placement=channel_completed")
            .retrieve()
            .awaitBody<KAKAOCompleteWebtoonDTO>()
        response.data.flatMap { section ->
            section.cardGroups.flatMap { cardGroup ->
                cardGroup.cards.map { card ->
                    card.content.id to card.content.title
                }
            }
        }
    }

    private fun convertToDayOfWeek(dayOfWeek: String): DayOfWeek = when (dayOfWeek) {
        "월" -> DayOfWeek.MONDAY
        "화" -> DayOfWeek.TUESDAY
        "수" -> DayOfWeek.WEDNESDAY
        "목" -> DayOfWeek.THURSDAY
        "금" -> DayOfWeek.FRIDAY
        "토" -> DayOfWeek.SATURDAY
        "일" -> DayOfWeek.SUNDAY
        else -> throw IllegalArgumentException("Invalid day of week: $dayOfWeek")
    }
}