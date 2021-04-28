package com.yandex.travelmap.service

import com.yandex.travelmap.dto.CitiesByCountryRequest
import com.yandex.travelmap.dto.CityResponse
import com.yandex.travelmap.repository.CityRepository
import org.springframework.stereotype.Service


@Service
class CitiesService(
    private val cityRepository: CityRepository
) {

    fun getCitiesByCountry(request: CitiesByCountryRequest): List<CityResponse> {
        return cityRepository.findByCountryIso(request.iso).map { city -> CityResponse(request.iso, city.name) }
    }
}