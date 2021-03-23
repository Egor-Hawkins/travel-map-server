package com.yandex.travelmap.controller

import com.yandex.travelmap.dto.RegistrationRequest
import org.springframework.web.bind.annotation.RestController
import com.yandex.travelmap.service.RegistrationService
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PostMapping
import java.lang.IllegalStateException


@RestController
class RegistrationController(private val registrationService: RegistrationService) {

    @PostMapping("/registration")
    fun register(@RequestBody request: RegistrationRequest?): String? {
        return request?.let {
            return try {
                if(registrationService.register(it)) {
                    "Registered successfully"
                } else {
                    "Registration failed: something went wrong"
                }
            } catch (e: IllegalStateException) {
                "Registration failed: ${e.message}"
            }
        }
    }
}