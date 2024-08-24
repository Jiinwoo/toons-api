package dev.woos.toons_api.api

import dev.woos.toons_api.api.dto.EmailVerifyDto
import dev.woos.toons_api.api.dto.MemberDto
import dev.woos.toons_api.usecase.MemberService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/members")
class MemberApi(
    private val memberService: MemberService
) {
    @GetMapping("/me")
    suspend fun getMe(
        @AuthenticationPrincipal userDetails: UserDetails,
    ): MemberDto {
        val member = memberService.getMe(userDetails.username)
        return MemberDto(
            id = member.id,
            name = member.name,
            provider = member.provider,
            verifiedEmail = member.verifiedEmail,
            subscribe = member.subscribe,
        )
    }

    @PostMapping("/subscribe")
    suspend fun subscribe(
        @AuthenticationPrincipal userDetails: UserDetails,
    ) {
        memberService.subscribe(userDetails.username.toLong())
    }

    @PostMapping("/unsubscribe")
    suspend fun unsubscribe(
        @AuthenticationPrincipal userDetails: UserDetails,
    ) {
        memberService.unsubscribe(userDetails.username.toLong())
    }

    @PostMapping("/verify")
    suspend fun verifyEmail(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody dto: EmailVerifyDto,
    ) {
        memberService.verifyEmail(userDetails.username, dto.email)
    }
}