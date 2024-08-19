package dev.woos.toons_api.api.dto

import dev.woos.toons_api.domain.board.ContentType
import java.time.LocalDateTime

data class PostDto(
    val id: Long,
    val title: String,
    val content: String,
    val username: String,
    val tag: String?,
    val isLiked: Boolean? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class PostCreateDto(
    val title: String,
    val content: String,
    val contentType: ContentType = ContentType.GENERAL,
    val contentId: Long? = null,
)

data class PostUpdateDto(
    val title: String,
    val content: String,
    val contentType: ContentType,
    val contentId: Long?,
)
