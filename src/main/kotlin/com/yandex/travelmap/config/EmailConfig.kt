package com.yandex.travelmap.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@ConfigurationProperties(prefix = "email", ignoreUnknownFields = true)
@PropertySource(value = ["classpath:application.properties"])
class EmailConfig {
    var confirmation: Boolean = false
}