package dev.woos.toons_api.config.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val secretKey: String,
    @Value("\${jwt.expiration}") private val expiration: Long
) {

    private val signedKey = Keys.hmacShaKeyFor(secretKey.toByteArray())

    fun generateToken(userId: Long): String {
        return Jwts.builder()
            .claims(emptyMap<String, Any>())
            .subject(userId.toString())
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expiration))
            .signWith(signedKey)
            .compact()
    }

    fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(signedKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}