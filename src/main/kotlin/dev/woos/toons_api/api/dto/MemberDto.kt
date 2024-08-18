package dev.woos.toons_api.api.dto

import dev.woos.toons_api.domain.member.AuthProvider

data class MemberDto (
    val id: Long,
    val name: String,
    val provider: AuthProvider
)