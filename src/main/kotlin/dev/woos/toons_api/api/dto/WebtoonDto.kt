package dev.woos.toons_api.api.dto

import dev.woos.toons_api.domain.common.Platform
import java.time.DayOfWeek

data class WebtoonDto (
    val id: Long,
    val title: String,
    val thumbnailUrl: String,
    val platform: Platform,
    val dayOfWeek: DayOfWeek,
)