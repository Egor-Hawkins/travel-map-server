package com.yandex.travelmap

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@ConfigurationProperties(prefix = "jwt", ignoreUnknownFields = true)
@PropertySource(value = ["classpath:custom-props.properties"])
class CustomConfig {
    var secret: String? = null
    var expirationTime: Int? = null
}
