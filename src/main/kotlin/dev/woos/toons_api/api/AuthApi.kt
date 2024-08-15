package dev.woos.toons_api.api

import dev.woos.toons_api.api.dto.GoogleLoginDto
import dev.woos.toons_api.api.dto.KakaoLoginDto
import dev.woos.toons_api.api.dto.LoginResDto
import dev.woos.toons_api.config.security.GoogleTokenVerifier
import dev.woos.toons_api.config.security.JwtTokenProvider
import dev.woos.toons_api.config.security.KakaoTokenVerifier
import dev.woos.toons_api.domain.member.AuthProvider
import dev.woos.toons_api.usecase.MemberService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthApi(
    private val googleTokenVerifier: GoogleTokenVerifier,
    private val kakaoTokenVerifier: KakaoTokenVerifier,
    private val memberService: MemberService,
    private val jwtTokenProvider: JwtTokenProvider,
) {

    @PostMapping("/google")
    suspend fun googleAuth(
        @RequestBody dto: GoogleLoginDto
    ): LoginResDto {
        val payload = googleTokenVerifier.verify(dto.idToken) ?: throw Exception("Invalid token")
        val member = memberService.findMemberOrCreate(
            provider = AuthProvider.GOOGLE,
            providerId = payload.subject,
            name = payload.email,
        )
        return LoginResDto(
            jwtTokenProvider.generateToken(member.id)
        )
    }

    @PostMapping("/kakao")
    suspend fun kakaoAuth(
        @RequestBody dto: KakaoLoginDto
    ): LoginResDto {
        val jwt = kakaoTokenVerifier.verify(dto.idToken)

        val member = memberService.findMemberOrCreate(
            provider = AuthProvider.KAKAO,
            providerId = jwt.subject,
            name = dto.nickname,
        )
        return LoginResDto(
            jwtTokenProvider.generateToken(member.id)
        )
    }

}