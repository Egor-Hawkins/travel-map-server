package com.yandex.travelmap.service

import com.yandex.travelmap.model.AppUser
import com.yandex.travelmap.util.EmailValidator
import org.springframework.stereotype.Service
import java.lang.IllegalStateException

data class RegistrationRequest(val email: String, val username: String, val password: String) //TODO???

@Service
class RegistrationService(
    private val userService: UserService,
    private val emailValidator: EmailValidator
) {

    fun register(registrationRequest: RegistrationRequest): String {
        val isEmailValid: Boolean = emailValidator.validate(registrationRequest.email)

        if (!isEmailValid) {
            throw IllegalStateException("email not valid")
        }
        val newAppUser = AppUser()
        newAppUser.email = registrationRequest.email
        newAppUser.username = registrationRequest.username
        newAppUser.password = registrationRequest.password
        return userService.registerUser(newAppUser)
    }

}