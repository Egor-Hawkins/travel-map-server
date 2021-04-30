package com.yandex.travelmap.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PingController {
    @GetMapping("/api/ping")
    fun ping(): String {
        return "ping ok"
    }

    @GetMapping("/health")
    fun health(): String {
        return "I am health!"
    }
}
