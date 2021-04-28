package com.yandex.travelmap.exception


import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class CityNotFoundException : ResponseStatusException(HttpStatus.NOT_FOUND)