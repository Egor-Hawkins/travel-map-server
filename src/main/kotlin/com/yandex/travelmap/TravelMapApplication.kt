package com.yandex.travelmap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class TravelMapApplication

fun main(args: Array<String>) {
    runApplication<TravelMapApplication>(*args)
}

@RestController
class PingController {
    @GetMapping
    fun ping(): String {
        return "ping ok"
    }
}