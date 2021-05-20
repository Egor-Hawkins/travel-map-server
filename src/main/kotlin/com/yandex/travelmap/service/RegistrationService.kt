package com.yandex.travelmap.service

import com.yandex.travelmap.config.EmailConfig
import com.yandex.travelmap.dto.RegistrationRequest
import com.yandex.travelmap.exception.EmailNotValidException
import com.yandex.travelmap.util.EmailValidator
import com.yandex.travelmap.util.MailBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


@Service
class RegistrationService(
    private val userService: UserService,
    private val emailValidator: EmailValidator,
    private val emailService: EmailService,
    private val confirmationTokenService: ConfirmationTokenService,
    private val mailBuilder: MailBuilder,
    private val emailConfig: EmailConfig
) {

    fun register(registrationRequest: RegistrationRequest): Boolean {
        val isEmailValid: Boolean = emailValidator.validate(registrationRequest.email)
        if (!isEmailValid) {
            throw EmailNotValidException()
        }
        val token = userService.registerUser(registrationRequest) ?: return false
        val link = "http://localhost:8080/registration/confirm?token=$token" //TODO change address
        if (emailConfig.confirmation) {
            emailService.send(
                registrationRequest.email,
                mailBuilder.buildEmail(link)
            )
        }
        return true
    }

    @Transactional
    fun confirmRegistration(token: String?): Boolean {
        if (token == null) {
            return false
        }
        val confirmationToken = confirmationTokenService.getToken(token)
            .orElseThrow { IllegalStateException("Token not found") }
        if (confirmationToken.confirmedAt != null) {
            throw IllegalStateException("Email already confirmed")
        }
        println(confirmationToken.expiresAt)
        println(LocalDateTime.now())
        if (!confirmationToken.expiresAt.isBefore(LocalDateTime.now())) {
            throw IllegalStateException("Token expired")
        }
        confirmationTokenService.confirm(confirmationToken)
        confirmationToken.appUser?.username?.let { userService.enableAppUser(it) }
        return true
    }
}
