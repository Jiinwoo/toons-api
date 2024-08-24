package dev.woos.toons_api.api.dto

data class AlarmDto(
    val id: Long,
    val webtoon: WebtoonDto,
)

data class AlarmCreateDto(
    val webtoonId: Long,
)