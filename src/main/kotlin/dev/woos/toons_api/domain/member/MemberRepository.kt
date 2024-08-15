package dev.woos.toons_api.domain.member

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MemberRepository: CoroutineCrudRepository<Member, Long> {
    suspend fun findByProviderAndProviderId(provider: AuthProvider, providerId: String): Member?

}