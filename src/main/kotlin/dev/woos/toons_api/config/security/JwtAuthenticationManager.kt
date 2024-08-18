package dev.woos.toons_api.config.security

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*
import kotlin.math.log

@Component
class JwtAuthenticationManager : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.just<Any>(authentication)
            .cast(JwtToken::class.java)
            .filter { jwtToken: JwtToken ->
                jwtToken.expiration.after(Date())
            }
            .map { jwtToken: JwtToken ->
                jwtToken.withAuthenticated(
                    true
                )
            }
            .switchIfEmpty(Mono.error(JwtAuthenticationException("Invalid token.")));
    }
}