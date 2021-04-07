package com.yandex.travelmap.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import javax.mail.MessagingException


@Service
class EmailService(
    private val mailSender: JavaMailSender
) {
    @Async
    fun send(to: String, email: String) {
        try {
            val mimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(mimeMessage, "utf-8")
            helper.setText(email, true)
            helper.setTo(to)
            helper.setSubject("Confirm your email")
            helper.setFrom("maslo@maslo.com")
            mailSender.send(mimeMessage)
        } catch (e: MessagingException) {
            throw IllegalStateException("failed to send email")
        }
    }

}