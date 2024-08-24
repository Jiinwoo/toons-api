package dev.woos.toons_api.api.dto

import dev.woos.toons_api.domain.member.AuthProvider

data class MemberDto(
    val id: Long,
    val name: String,
    val provider: AuthProvider,
    val verifiedEmail: String?,
    val subscribe: Boolean
)

data class EmailVerifyDto(
    val email: String,
)