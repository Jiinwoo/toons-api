package dev.woos.toons_api.usecase

import dev.woos.toons_api.domain.member.AuthProvider
import dev.woos.toons_api.domain.member.Member
import dev.woos.toons_api.domain.member.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class MemberService(
    private val memberRepository: MemberRepository,
){
    suspend fun findMemberOrCreate(
        provider: AuthProvider,
        providerId: String,
        name: String,
    ): Member{
        return memberRepository.findByProviderAndProviderId(provider, providerId)
            ?: memberRepository.save(
                Member(
                    name = name,
                    provider = provider,
                    providerId = providerId,
                )
            )
    }
}