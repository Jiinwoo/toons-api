package dev.woos.toons_api.api

import dev.woos.toons_api.api.dto.PostDto
import dev.woos.toons_api.api.dto.WebtoonDto
import dev.woos.toons_api.usecase.QueryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class HomeDto(
    val hotPosts: List<HostPostDto>,
    val completedWebtoons: List<WebtoonDto>,
    val topAlarmWebtoons: List<WebtoonDto>
) {
    data class HostPostDto(
        val id: Long,
        val title: String,
    )
}

@RestController
@RequestMapping("/api/home")
class HomeApi(
    private val queryService: QueryService,
) {
    @GetMapping
    suspend fun queryHome(): HomeDto {
        return queryService.queryHome()
    }
}