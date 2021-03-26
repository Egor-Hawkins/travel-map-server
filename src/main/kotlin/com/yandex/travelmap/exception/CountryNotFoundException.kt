package com.yandex.travelmap.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class CountryNotFoundException : ResponseStatusException(HttpStatus.NOT_FOUND)