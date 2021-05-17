package com.yandex.travelmap.controller

import com.yandex.travelmap.dto.CityResponse
import com.yandex.travelmap.dto.CitiesByCountryListRequest
import com.yandex.travelmap.service.CitiesService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cities")
class CitiesController(
    private val citiesService: CitiesService
) {
    @PostMapping
    fun getCitiesByCountry(@RequestBody request: CitiesByCountryListRequest): List<CityResponse> {
        return citiesService.getCitiesByCountry(request.iso)
    }
}