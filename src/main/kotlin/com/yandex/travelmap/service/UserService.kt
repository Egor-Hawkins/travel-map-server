package com.yandex.travelmap.service

import com.yandex.travelmap.model.AppUser
import com.yandex.travelmap.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import com.yandex.travelmap.dto.CountryResponse
import com.yandex.travelmap.dto.RegistrationRequest
import com.yandex.travelmap.dto.VisitedCountryRequest
import com.yandex.travelmap.exception.CountryNotFoundException
import com.yandex.travelmap.exception.UserNotFoundException
import com.yandex.travelmap.repository.CountryRepository
import org.springframework.stereotype.Service


@Service
class UserService(
    private val userRepository: UserRepository,
    private val countryRepository: CountryRepository,
    private val passwordEncoder: PasswordEncoder
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

    fun registerUser(registrationRequest: RegistrationRequest): Boolean {
        val emailExists = userRepository.findByEmail(registrationRequest.email).isPresent
        if (emailExists) {
            throw IllegalStateException("User with this email already exists")
        }
        val usernameExists = userRepository.findByUsername(registrationRequest.username).isPresent
        if (usernameExists) {
            throw IllegalStateException("User with this name already exists")
        }
        val encodedPassword: String = passwordEncoder.encode(registrationRequest.password)
        val appUser = AppUser(
            email = registrationRequest.email,
            username = registrationRequest.username,
            password = encodedPassword,
            enabled = true
        ) //TODO send confirmation?
        userRepository.save(appUser)
        return true
    }
}
