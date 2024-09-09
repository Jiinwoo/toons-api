package dev.woos.toons_api.usecase

import dev.woos.toons_api.api.dto.PostCreateDto
import dev.woos.toons_api.api.dto.PostDto
import dev.woos.toons_api.api.dto.PostUpdateDto
import dev.woos.toons_api.domain.board.*
import dev.woos.toons_api.domain.member.MemberRepository
import dev.woos.toons_api.domain.webtoon.WebtoonRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostService(
    private val postRepository: PostRepository,
    private val postLikeRepository: PostLikeRepository,
    private val webtoonRepository: WebtoonRepository,
    private val memberRepository: MemberRepository,
) {
    @Transactional(readOnly = true)
    suspend fun getAll(pageable: Pageable, boardId: Long): Page<PostDto> = coroutineScope {
        val list = async {
            postRepository.findAllByBoardIdWithMemberAndWebtoonAndDeletedAtIsNull(
                size = pageable.pageSize,
                offset = pageable.offset,
                boardId = boardId
            )
        }
        val count = async {
            postRepository.countByBoardIdAndDeletedAtIsNull(boardId)
        }

        PageImpl(
            list.await().map { post ->
                PostDto(
                    id = post.id,
                    title = post.title,
                    content = post.content,
                    username = post.member.name,
                    tag = post.tag,
                    createdAt = post.createdAt,
                    updatedAt = post.updatedAt,
                )
            }.toList(),
            pageable,
            count.await()
        )
    }

    @Transactional
    suspend fun create(userDetails: UserDetails, boardId: Long, dto: PostCreateDto): Long {
        val tag = when (dto.contentType) {
            ContentType.WEBNOVEL, ContentType.GENERAL -> {
                null
            }

            ContentType.WEBTOON -> {
                val webtoon =
                    webtoonRepository.findById(dto.contentId!!) ?: throw IllegalArgumentException("Webtoon not found")
                webtoon.title
            }
        }

        val post = postRepository.save(
            Post(
                boardId = boardId,
                memberId = userDetails.username.toLong(),
                title = dto.title,
                content = dto.content,
                tag = tag,
            )
        )

        return post.id
    }

    @Transactional(readOnly = true)
    suspend fun get(postId: Long): PostDto {
        val post = postRepository.findByIdAndDeletedAtIsNull(postId) ?: throw IllegalArgumentException("Post not found")
        val member = memberRepository.findById(post.memberId) ?: throw IllegalArgumentException("Member not found")
        post.member = member
        return PostDto(
            id = post.id,
            title = post.title,
            content = post.content,
            username = post.member.name,
            tag = post.tag,
            createdAt = post.createdAt,
            updatedAt = post.updatedAt,
        )
    }

    @Transactional(readOnly = true)
    suspend fun getWithLike(postId: Long, username: String): PostDto {
        val post = postRepository.findByIdAndDeletedAtIsNull(postId) ?: throw IllegalArgumentException("Post not found")
        val author = memberRepository.findById(post.memberId) ?: throw IllegalArgumentException("Member not found")
        post.member = author
        val postLike = postLikeRepository.findByPostIdAndMemberId(post.id, username.toLong())

        return PostDto(
            id = post.id,
            title = post.title,
            content = post.content,
            username = post.member.name,
            tag = post.tag,
            isLiked = postLike != null,
            createdAt = post.createdAt,
            updatedAt = post.updatedAt,
        )

    }

    @Transactional
    suspend fun delete(userDetails: UserDetails, postId: Long) {
        val post = postRepository.findById(postId) ?: throw IllegalArgumentException("Post not found")
        if (post.memberId != userDetails.username.toLong()) {
            throw IllegalArgumentException("You are not the owner of this post")
        }
        post.delete()
        postRepository.save(post)
    }

    suspend fun update(userDetails: UserDetails, postId: Long, dto: PostUpdateDto): Long {
        val post = postRepository.findByIdAndDeletedAtIsNull(postId) ?: throw IllegalArgumentException("Post not found")
        if (post.memberId != userDetails.username.toLong()) {
            throw IllegalArgumentException("You are not the owner of this post")
        }
        post.title = dto.title
        post.content = dto.content
        post.tag = when (dto.contentType) {
            ContentType.WEBNOVEL, ContentType.GENERAL -> {
                null
            }

            ContentType.WEBTOON -> {
                val webtoon =
                    webtoonRepository.findById(dto.contentId!!) ?: throw IllegalArgumentException("Webtoon not found")
                webtoon.title
            }
        }
        postRepository.save(post)
        return post.id
    }

    @Transactional
    suspend fun like(username: String, postId: Long) {
        val post = postRepository.findByIdAndDeletedAtIsNull(postId) ?: throw IllegalArgumentException("Post not found")
        post.increaseLikeCount()
        val postLike = PostLike(
            postId = postId,
            memberId = username.toLong()
        )
        postRepository.save(post)
        postLikeRepository.save(postLike)

    }

    @Transactional
    suspend fun unlike(username: String, postId: Long) {
        val post = postRepository.findByIdAndDeletedAtIsNull(postId) ?: throw IllegalArgumentException("Post not found")
        post.decreaseLikeCount()
        postRepository.save(post)
        postLikeRepository.deleteByPostIdAndMemberId(postId, username.toLong())
    }


}
