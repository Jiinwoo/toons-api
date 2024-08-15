package dev.woos.toons_api.config.security

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GoogleTokenVerifier(
    @Value("\${google.client_id}")
    private val clientId: String,
) {
    private val logger = KotlinLogging.logger {  }

    private val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    private val jsonFactory = GsonFactory.getDefaultInstance()

    private val verifier = GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
        .setAudience(listOf(clientId))
        .build()


    fun verify(token: String): GoogleIdToken.Payload? {
        val idToken = verifier.verify(token)
        logger.info { "idToken: ${idToken.payload}" }

        return idToken?.payload
    }
}