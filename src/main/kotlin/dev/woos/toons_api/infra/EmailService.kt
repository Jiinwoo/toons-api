package dev.woos.toons_api.infra

import jakarta.mail.internet.MimeMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context


@Component
class EmailService(
    private val mailSender: JavaMailSender,
    private val templateEngine: TemplateEngine,
    @Value("\${server.url}") private val serverUrl: String
) {
    data class WebtoonEmailDto(
        val link: String,
        val thumbnailUrl: String,
        val title: String
    )


    @Transactional
    suspend fun sendEndedWebtoonAlert(
        to: String,
        username: String,
        webtoons: List<WebtoonEmailDto>,
        memberId: Long,
    ): Result<Unit> = runCatching {
        val message: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")

        helper.setFrom("toons@woos.dev")
        helper.setTo(to)
        helper.setSubject("완결 웹툰 소식")

        val context = Context().apply {
            setVariable("userName", username)
            setVariable("webtoons", webtoons)
            setVariable("unsubscribeLink", "${serverUrl}/unsubscribe/${memberId}")
        }

        val htmlContent = withContext(Dispatchers.Default) {
            templateEngine.process("webtoon-email-template", context)
        }
        helper.setText(htmlContent, true)

        withContext(Dispatchers.IO) {
            mailSender.send(message)
        }

    }

    suspend fun sendVerificationEmail(token: String, username: String, email: String) {
        val message: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")

        helper.setFrom("toons@woos.dev")
        helper.setTo(email)
        helper.setSubject("이메일 인증")

        val context = Context().apply {
            setVariable("userName", username)
            setVariable("verificationLink", "${serverUrl}/api/subscribe?token=$token&email=$email")
        }

        val htmlContent = withContext(Dispatchers.Default) {
            templateEngine.process("verify-email-template", context)
        }

        helper.setText(htmlContent, true)

        withContext(Dispatchers.IO) {
            mailSender.send(message)
        }
    }

}