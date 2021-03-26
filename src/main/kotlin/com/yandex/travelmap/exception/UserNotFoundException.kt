package com.yandex.travelmap.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class UserNotFoundException(reason: String) : ResponseStatusException(HttpStatus.NOT_FOUND, reason)