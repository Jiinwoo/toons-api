package dev.woos.toons_api.config.security

import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@Component
class JwtServerAuthenticationConverter(
    private val jwtTokenProvider: JwtTokenProvider,
) : ServerAuthenticationConverter {
    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        return Mono.justOrEmpty(
            exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        )
            .filter {
                it.startsWith("Bearer ")
            }
            .map { it.substring(7) }
            .map { token ->
                val claims = jwtTokenProvider.getClaims(token)
                JwtToken(
                    user = createUserDetails(claims.subject, emptyList()),
                    expiration = claims.expiration
                )

            }
    }

    private fun createUserDetails(subject: String, authorities: List<SimpleGrantedAuthority>): UserDetails {
        return User.builder()
            .username(subject)
            .authorities(authorities)
            .password("")
            .build()
    }
}