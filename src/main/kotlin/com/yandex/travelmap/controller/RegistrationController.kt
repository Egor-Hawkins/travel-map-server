package com.yandex.travelmap.controller

import org.springframework.web.bind.annotation.RestController
import com.yandex.travelmap.service.RegistrationRequest
import com.yandex.travelmap.service.RegistrationService

import org.springframework.web.bind.annotation.RequestBody

import org.springframework.web.bind.annotation.PostMapping


@RestController
class RegistrationController(private val registrationService: RegistrationService) {

    @PostMapping("/registration")
    fun register(@RequestBody request: RegistrationRequest?): String? {
        return request?.let { registrationService.register(it) }
    }
}