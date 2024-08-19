package dev.woos.toons_api.domain.board

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.LocalDateTime

interface PostRepository: CoroutineCrudRepository<Post, Long>, CustomPostRepository{
    suspend fun findByIdAndDeletedAtIsNull(id: Long): Post?
    suspend fun countByBoardIdAndDeletedAtIsNull(boardId: Long): Long
    suspend fun findTop5ByCreatedAtAfterOrderByLikeCountDesc(from: LocalDateTime): Flow<Post>
}