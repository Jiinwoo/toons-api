package dev.woos.toons_api.config.security

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.net.URL
import java.security.interfaces.RSAPublicKey
import java.util.concurrent.TimeUnit

@Component
class KakaoTokenVerifier {

    private val logger = KotlinLogging.logger {}

    private val provider: JwkProvider by lazy {
        JwkProviderBuilder(URL("https://kauth.kakao.com/.well-known/jwks.json"))
            .cached(10, 7, TimeUnit.DAYS)
            .build()
    }

    fun verify(idToken: String): DecodedJWT {
        val decoded = JWT.decode(idToken)
        val jwk = provider[decoded.keyId]

        val algorithm = Algorithm.RSA256(jwk.publicKey as RSAPublicKey, null)

        val verifier = JWT.require(algorithm).build()

        return verifier.verify(idToken)
    }


}