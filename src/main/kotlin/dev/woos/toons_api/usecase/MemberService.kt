package dev.woos.toons_api.usecase

import dev.woos.toons_api.domain.member.AuthProvider
import dev.woos.toons_api.domain.member.Member
import dev.woos.toons_api.domain.member.MemberRepository
import dev.woos.toons_api.infra.EmailService
import dev.woos.toons_api.utils.TokenUtil
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val emailService: EmailService,
    private val tokenUtil: TokenUtil,
) {
    suspend fun findMemberOrCreate(
        provider: AuthProvider,
        providerId: String,
        name: String,
    ): Member {
        return memberRepository.findByProviderAndProviderId(provider, providerId)
            ?: memberRepository.save(
                Member(
                    name = name,
                    provider = provider,
                    providerId = providerId,
                )
            )
    }

    suspend fun getMe(username: String): Member {
        return memberRepository.findById(username.toLong()) ?: throw Exception("Not Found")
    }

    @Transactional
    suspend fun unsubscribe(id: Long) {
        memberRepository.findById(id)?.let {
            it.unsubscribe()
            memberRepository.save(it)
        } ?: throw Exception("Not Found")
    }

    @Transactional
    suspend fun subscribe(id: Long, email: String) {
        memberRepository.findById(id)?.let {
            it.subscribe = true
            it.verifiedEmail = email
            memberRepository.save(it)
        } ?: throw Exception("Not Found")
    }

    @Transactional
    suspend fun subscribe(id: Long) {
        memberRepository.findById(id)?.let {
            it.subscribe = true
            memberRepository.save(it)
        } ?: throw Exception("Not Found")
    }

    @Transactional(readOnly = true)
    suspend fun verifyEmail(username: String, email: String) {
        val member = memberRepository.findById(username.toLong()) ?: throw Exception("Not Found")
        val token = tokenUtil.generateToken(member.id)
        coroutineScope {
            emailService.sendVerificationEmail(token, member.name, email)
        }

    }
}