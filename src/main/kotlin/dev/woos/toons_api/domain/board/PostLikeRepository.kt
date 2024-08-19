package dev.woos.toons_api.domain.board

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostLikeRepository: CoroutineCrudRepository<PostLike, Long> {
    suspend fun deleteByPostIdAndMemberId(postId: Long, memberId: Long)
}