package dev.woos.toons_api.config.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

class JwtToken(
    private val user: UserDetails,
    val expiration: Date,
): AbstractAuthenticationToken(emptyList()) {
    fun withAuthenticated(isAuthenticated: Boolean): Authentication {
        val cloned = JwtToken(user, expiration)
        cloned.isAuthenticated = isAuthenticated
        return cloned
    }

    override fun getCredentials(): Any? {
        return null
    }


    override fun getPrincipal(): Any {
        return user
    }
}
