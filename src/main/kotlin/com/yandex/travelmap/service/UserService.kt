package com.yandex.travelmap.service

import com.yandex.travelmap.dto.CountryResponse
import com.yandex.travelmap.dto.VisitedCountryRequest
import com.yandex.travelmap.exception.CountryNotFoundException
import com.yandex.travelmap.exception.UserNotFoundException
import com.yandex.travelmap.repository.CountryRepository
import com.yandex.travelmap.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val countryRepository: CountryRepository
) {
    fun getVisitedCountries(username: String): List<CountryResponse> {
        return userRepository.findByUsername(username).map {
            it.visitedCountries.map { country -> CountryResponse(country.iso, country.name) }
        }.orElseThrow {
            UserNotFoundException("Wrong user id")
        }
    }

    fun addVisitedCountry(username: String, countryRequest: VisitedCountryRequest) {
        return userRepository.findByUsername(username).orElseThrow {
            UserNotFoundException("Wrong user id")
        }.let { user ->
            countryRepository.findByIso(countryRequest.iso).orElseThrow {
                CountryNotFoundException()
            }.let { country ->
                user.visitedCountries.add(country)
            }
        }
    }

    fun deleteVisitedCountry(username: String, countryResponse: VisitedCountryRequest) {
        return userRepository.findByUsername(username).orElseThrow {
            UserNotFoundException("Wrong user id")
        }.let { user ->
            countryRepository.findByIso(countryResponse.iso).orElseThrow {
                CountryNotFoundException()
            }.let { country ->
                user.visitedCountries.remove(country)
            }
        }
    }
}