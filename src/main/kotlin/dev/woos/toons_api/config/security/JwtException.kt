package dev.woos.toons_api.config.security

import org.springframework.security.core.AuthenticationException


internal class JwtAuthenticationException(msg: String) : AuthenticationException(msg)