package com.yandex.travelmap.controller

import com.yandex.travelmap.dto.RegistrationRequest
import com.yandex.travelmap.exception.EmailNotValidException
import com.yandex.travelmap.service.RegistrationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/registration")
class RegistrationController(
    private val registrationService: RegistrationService
) {

    @PostMapping
    fun register(@RequestBody request: RegistrationRequest): ResponseEntity<String> {
        return try {
            if (registrationService.register(request)) {
                ResponseEntity("Registration successful", HttpStatus.OK)
            } else {
                ResponseEntity("Registration failed: something went wrong", HttpStatus.INTERNAL_SERVER_ERROR)
            }
        } catch (e: IllegalStateException) {
            ResponseEntity("Registration failed: ${e.message}", HttpStatus.CONFLICT)
        } catch (e: EmailNotValidException) {
            ResponseEntity("Email not valid", HttpStatus.CONFLICT)
        }
    }

    @GetMapping("/confirm")
    fun confirm(@RequestParam("token") token: String?): ResponseEntity<String> {
        return try {
            registrationService.confirmRegistration(token)
            ResponseEntity("Registration confirmed", HttpStatus.OK)
        } catch (e: IllegalStateException) {
            ResponseEntity("Confirmation failed: ${e.message}", HttpStatus.CONFLICT)
        }
    }
}
