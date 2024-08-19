package dev.woos.toons_api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(
        http: ServerHttpSecurity,
        authenticationManager: ReactiveAuthenticationManager,
        authenticationConverter: ServerAuthenticationConverter,
    ): SecurityWebFilterChain {

        val webFilter = AuthenticationWebFilter(authenticationManager)
        webFilter.setServerAuthenticationConverter(authenticationConverter)

        http
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) //session STATELESS
            .authorizeExchange { authorizeRequests ->
                authorizeRequests
                    .pathMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                    .pathMatchers("/api/webtoons/**").permitAll()
                    .pathMatchers("/api/home/**").permitAll()
                    .pathMatchers("/api/auth/**").permitAll()
                    .anyExchange().authenticated()
            }
            .addFilterAt(webFilter, SecurityWebFiltersOrder.AUTHENTICATION)


        return http.build()
    }
}