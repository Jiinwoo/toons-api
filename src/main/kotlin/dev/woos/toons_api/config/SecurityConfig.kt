package dev.woos.toons_api.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    @Value("\${cors.allowed-origins}") private val allowedOrigins: List<String>
) {

    @Bean
    fun securityFilterChain(
        http: ServerHttpSecurity,
        authenticationManager: ReactiveAuthenticationManager,
        authenticationConverter: ServerAuthenticationConverter,
    ): SecurityWebFilterChain {

        val webFilter = AuthenticationWebFilter(authenticationManager)
        webFilter.setServerAuthenticationConverter(authenticationConverter)

        http
            .cors {}
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) //session STATELESS
            .authorizeExchange { authorizeRequests ->
                authorizeRequests
                    .pathMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                    .pathMatchers("/api/webtoons/**").permitAll()
                    .pathMatchers("/api/subscribe").permitAll()
                    .pathMatchers("/api/unsubscribe").permitAll()
                    .pathMatchers("/api/home/**").permitAll()
                    .pathMatchers("/api/auth/**").permitAll()
                    .anyExchange().authenticated()
            }
            .addFilterAt(corsWebFilter(), SecurityWebFiltersOrder.CORS)
            .addFilterAt(webFilter, SecurityWebFiltersOrder.AUTHENTICATION)


        return http.build()
    }

    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val corsConfig = CorsConfiguration()

        corsConfig.allowedOrigins = allowedOrigins // 허용할 오리진 설정
        corsConfig.maxAge = 8000L
        corsConfig.allowCredentials = true // 쿠키 허용
        corsConfig.addAllowedMethod("*") // 모든 HTTP 메서드 허용
        corsConfig.addAllowedHeader("*") // 모든 헤더 허용

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfig) // 모든 경로에 CORS 설정 적용

        return CorsWebFilter(source)
    }
}