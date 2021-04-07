package com.yandex.travelmap.controller

import com.yandex.travelmap.dto.RegistrationRequest
import com.yandex.travelmap.service.RegistrationService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/registration")
class RegistrationController(
    private val registrationService: RegistrationService) {

    @PostMapping
    fun register(@RequestBody request: RegistrationRequest): String {
        return try {
            if (registrationService.register(request)) {
                "Registration successful"
            } else {
                "Registration failed: something went wrong"
            }
        } catch (e: IllegalStateException) {
            "Registration failed: ${e.message}"
        }
    }

    @GetMapping("/confirm")
    fun confirm(@RequestParam("token") token: String?): String? {
        try {
            registrationService.confirmRegistration(token)
            return "Registration confirmed"
        } catch (e: IllegalStateException) {
            return "Confirmation failed: ${e.message}"
        }
    }
}
