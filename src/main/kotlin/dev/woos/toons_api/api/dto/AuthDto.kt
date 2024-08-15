package dev.woos.toons_api.api.dto

data class GoogleLoginDto (
    val idToken: String,
)

data class KakaoLoginDto (
    val idToken: String,
    val nickname: String
)

data class LoginResDto (
    val token: String
)