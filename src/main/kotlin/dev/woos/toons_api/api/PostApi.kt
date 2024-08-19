package dev.woos.toons_api.api

import dev.woos.toons_api.api.dto.PostCreateDto
import dev.woos.toons_api.api.dto.PostDto
import dev.woos.toons_api.api.dto.PostUpdateDto
import dev.woos.toons_api.usecase.PostService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/posts")
class PostApi(
    private val postService: PostService
) {

    @GetMapping
    suspend fun getPosts(
        @PageableDefault(size = 20) pageable: Pageable,
        @RequestParam("boardId") boardId: Long,
    ): Page<PostDto> {
        return postService.getAll(pageable, boardId)
    }

    @GetMapping("/{id}")
    suspend fun getPost(
        @PathVariable("id") postId: Long,
    ): PostDto {
        return postService.get(postId)
    }

    @PostMapping
    suspend fun createPost(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestParam("boardId") boardId: Long,
        @RequestBody dto: PostCreateDto,
    ): Long {
        return postService.create(userDetails, boardId, dto)
    }

    @PostMapping("/{id}/like")
    suspend fun likePost(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable("id") postId: Long,
    ) {
        postService.like(userDetails.username, postId)
    }

    @PostMapping("/{id}/unlike")
    suspend fun unlikePost(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable("id") postId: Long,
    ) {
        postService.unlike(userDetails.username, postId)
    }

    @PutMapping("/{id}")
    suspend fun updatePost(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable("id") postId: Long,
        @RequestBody dto: PostUpdateDto,
    ): Long {
        return postService.update(userDetails, postId, dto)
    }

    @DeleteMapping("/{id}")
    suspend fun deletePost(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable("id") postId: Long,
    ) {
        postService.delete(userDetails, postId)
    }

}