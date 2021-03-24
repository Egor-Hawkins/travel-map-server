package com.yandex.travelmap.util

import org.springframework.stereotype.Service

@Service
class EmailValidator {
    private val EMAIL_PATTERN = Regex("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$")

    fun validate(email: String): Boolean {
        return email.matches(EMAIL_PATTERN)
    }
}
