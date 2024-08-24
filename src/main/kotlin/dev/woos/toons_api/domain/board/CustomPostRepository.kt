package dev.woos.toons_api.domain.board

import kotlinx.coroutines.flow.Flow

interface CustomPostRepository {
    suspend fun findAllByBoardIdWithMemberAndWebtoonAndDeletedAtIsNull(
        size: Int,
        offset: Long,
        boardId: Long
    ): Flow<Post>
}