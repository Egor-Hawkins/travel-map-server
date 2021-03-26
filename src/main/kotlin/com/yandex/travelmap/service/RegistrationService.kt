package com.yandex.travelmap.service

import com.yandex.travelmap.dto.RegistrationRequest
import com.yandex.travelmap.model.AppUser
import com.yandex.travelmap.util.EmailValidator
import org.springframework.stereotype.Service
import java.lang.IllegalStateException

@Service
class RegistrationService(
    private val userService: UserService,
    private val emailValidator: EmailValidator
) {

    fun register(registrationRequest: RegistrationRequest): Boolean {
        val isEmailValid: Boolean = emailValidator.validate(registrationRequest.email)
        if (!isEmailValid) {
            throw IllegalStateException("Email not valid")
        }
        return userService.registerUser(registrationRequest)
    }
}
