package dev.woos.toons_api.utils

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


@Component
class TokenUtil {
    @Value("\${jwt.secret}")
    private lateinit var secretKey: String

    private val algorithm = "HmacSHA256"
    private val tokenValidityDays = 30L

    fun generateToken(memberId: Long): String {
        val timestamp = Instant.now().epochSecond
        val data = "$memberId:$timestamp"
        val signature = createSignature(data)
        return Base64.getUrlEncoder().encodeToString("$data:$signature".toByteArray())
    }

    fun getMemberIdIfVerified(token: String): Long? {
        return try {
            val decodedToken = String(Base64.getUrlDecoder().decode(token))
            val (memberId, timestamp, receivedSignature) = decodedToken.split(":")

            val data = "$memberId:$timestamp"
            val calculatedSignature = createSignature(data)

            val tokenTimestamp = timestamp.toLong()
            val currentTimestamp = Instant.now().epochSecond
            if (
                receivedSignature == calculatedSignature &&
                (currentTimestamp - tokenTimestamp <= tokenValidityDays * 24 * 60 * 60)
            ) {
                memberId.toLong()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun createSignature(data: String): String {
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(), algorithm)
        val mac = Mac.getInstance(algorithm)
        mac.init(secretKeySpec)
        val rawHmac = mac.doFinal(data.toByteArray())
        return Base64.getEncoder().encodeToString(rawHmac)
    }
}