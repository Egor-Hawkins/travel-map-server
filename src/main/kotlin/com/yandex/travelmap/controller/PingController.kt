package com.yandex.travelmap.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PingController {
    @GetMapping
    fun ping(): String {
        return "ping ok"
    }
}