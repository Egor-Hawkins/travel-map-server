package com.yandex.travelmap.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class NotAuthorizedException : ResponseStatusException(HttpStatus.UNAUTHORIZED)