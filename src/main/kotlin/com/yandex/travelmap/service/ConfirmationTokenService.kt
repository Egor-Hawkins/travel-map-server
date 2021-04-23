package com.yandex.travelmap.service

import com.yandex.travelmap.model.ConfirmationToken
import com.yandex.travelmap.repository.ConfirmationTokenRepository
import org.springframework.stereotype.Service
import java.lang.IllegalStateException
import java.time.LocalDateTime
import java.util.*

@Service
class ConfirmationTokenService(private val confirmationTokenRepository: ConfirmationTokenRepository) {
    fun saveConfirmationToken(token: ConfirmationToken) {
        confirmationTokenRepository.save(token)
    }

    fun getToken(token: String): Optional<ConfirmationToken> {
        return confirmationTokenRepository.findByToken(token)
    }

    fun confirm(confirmationToken: ConfirmationToken) {
        confirmationToken.confirmedAt = LocalDateTime.now()
        confirmationTokenRepository.save(confirmationToken)
    }
}