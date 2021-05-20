package com.yandex.travelmap.config

import org.springframework.context.annotation.Configuration

@Configuration
class EmailConfig {
    val confirmation: Boolean by lazy {
        System.getenv("EMAIL_CONFIRMATION")?.toBoolean() ?: false
    }
}