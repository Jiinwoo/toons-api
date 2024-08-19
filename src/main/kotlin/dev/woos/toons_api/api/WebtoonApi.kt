package dev.woos.toons_api.api

import dev.woos.toons_api.api.dto.WebtoonDto
import dev.woos.toons_api.domain.webtoon.WebtoonRepository
import dev.woos.toons_api.usecase.WebtoonService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/webtoons")
class WebtoonApi(
    private val webtoonService: WebtoonService,
) {

    @GetMapping
    suspend fun getWebtoons(
        @PageableDefault(size = 20) pageable: Pageable,
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) days: List<String>?,
        @RequestParam(required = false) platforms: List<String>?
    ): Page<WebtoonDto> {
        return webtoonService.findAllWithFilters(pageable, title, days, platforms)
    }

    @PostMapping("/naver")
    suspend fun getNaverWebtoons() {
        return webtoonService.crawlerNAVER()
    }

    @PostMapping("/naver-complete")
    suspend fun getCompletedNaverWebtoons() {
        return webtoonService.crawlerCompletedNAVER()
    }

    @PostMapping("/kakao")
    suspend fun getKakaoWebtoons() {
        return webtoonService.crawlerKAKAO()
    }

    @PostMapping("/kakao-complete")
    suspend fun getCompletedKakaoWebtoons() {
        return webtoonService.crawlerCompletedKAKAO()
    }
}