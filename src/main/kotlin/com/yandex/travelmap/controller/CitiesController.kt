package com.yandex.travelmap.controller

import com.yandex.travelmap.dto.CityResponse
import com.yandex.travelmap.dto.VisitedCitiesByCountryListRequest
import com.yandex.travelmap.service.CitiesService
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/cities")
class CitiesController(
    private val citiesService: CitiesService
) {
    @PutMapping
    fun getCitiesByCountry(@RequestBody request: VisitedCitiesByCountryListRequest): List<CityResponse> {
        return citiesService.getCitiesByCountry(request.iso)
    }
}