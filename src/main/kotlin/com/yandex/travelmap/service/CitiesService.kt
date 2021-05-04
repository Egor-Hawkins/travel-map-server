package com.yandex.travelmap.service

import com.yandex.travelmap.dto.CityResponse
import com.yandex.travelmap.repository.CityRepository
import org.springframework.stereotype.Service


@Service
class CitiesService(
    private val cityRepository: CityRepository
) {

    fun getCitiesByCountry(iso: String): List<CityResponse> {
        return cityRepository.findByCountryIso(iso).map { city -> CityResponse(iso, city.name) }
    }
}