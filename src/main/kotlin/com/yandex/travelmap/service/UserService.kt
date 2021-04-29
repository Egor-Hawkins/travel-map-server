package com.yandex.travelmap.service

import com.yandex.travelmap.config.EmailConfig
import com.yandex.travelmap.dto.*
import com.yandex.travelmap.exception.CityNotFoundException
import com.yandex.travelmap.exception.CountryNotFoundException
import com.yandex.travelmap.exception.UserNotFoundException
import com.yandex.travelmap.model.AppUser
import com.yandex.travelmap.model.ConfirmationToken
import com.yandex.travelmap.repository.CityRepository
import com.yandex.travelmap.repository.CountryRepository
import com.yandex.travelmap.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*


@Service
class UserService(
    private val userRepository: UserRepository,
    private val countryRepository: CountryRepository,
    private val cityRepository: CityRepository,
    private val passwordEncoder: PasswordEncoder,
    private val confirmationTokenService: ConfirmationTokenService,
    private val emailConfig: EmailConfig
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
                country.visitors.add(user)
                userRepository.save(user)
            }
        }
    }

    fun deleteVisitedCountry(username: String, countryRequest: VisitedCountryRequest) {
        return userRepository.findByUsername(username).orElseThrow {
            UserNotFoundException("Wrong user id")
        }.let { user ->
            countryRepository.findByIso(countryRequest.iso).orElseThrow {
                CountryNotFoundException()
            }.let { country ->
                user.visitedCountries.remove(country)
                country.visitors.remove(user)
                userRepository.save(user)
            }
        }
    }

    fun getVisitedCities(username: String, cityRequest: CitiesByCountryListRequest): List<CityResponse> {
        return userRepository.findByUsername(username).map {
            it.visitedCities.map { city -> CityResponse(city.country.iso, city.name) }
                .filter { response -> (cityRequest.iso == "" || cityRequest.iso == response.iso) }
        }.orElseThrow {
            UserNotFoundException("Wrong user id")
        }
    }

    fun addVisitedCity(username: String, cityRequest: VisitedCityRequest) {
        return userRepository.findByUsername(username).orElseThrow {
            UserNotFoundException("Wrong user id")
        }.let { user ->
            cityRepository.findByNameIgnoreCaseAndCountryIso(cityRequest.name, cityRequest.iso).orElseThrow {
                CityNotFoundException()
            }.let { city ->
                user.visitedCities.add(city)
                city.visitors.add(user)
                userRepository.save(user)
            }
        }
    }

    fun deleteVisitedCity(username: String, cityRequest: VisitedCityRequest) {
        return userRepository.findByUsername(username).orElseThrow {
            UserNotFoundException("Wrong user id")
        }.let { user ->
            cityRepository.findByNameIgnoreCaseAndCountryIso(cityRequest.name, cityRequest.iso).orElseThrow {
                CityNotFoundException()
            }.let { city ->
                user.visitedCities.remove(city)
                city.visitors.remove(user)
                userRepository.save(user)
            }
        }
    }

    fun registerUser(registrationRequest: RegistrationRequest): String? {
        val emailExists = userRepository.findByEmail(registrationRequest.email).isPresent
        if (emailExists) {
            throw IllegalStateException("User with this email already exists")
        }
        val usernameExists = userRepository.findByUsername(registrationRequest.username).isPresent
        if (usernameExists) {
            // TODO if email not confirmed send confirmation email.
            throw IllegalStateException("User with this name already exists")
        }
        val encodedPassword: String = passwordEncoder.encode(registrationRequest.password)
        val appUser = AppUser(
            email = registrationRequest.email,
            username = registrationRequest.username,
            password = encodedPassword,
            enabled = !emailConfig.confirmation
        )
        userRepository.save(appUser)

        val token = UUID.randomUUID().toString()
        val confirmationToken = ConfirmationToken(
            token = token,
            createdAt = LocalDateTime.now(),
            expiresAt = LocalDateTime.now().plusMinutes(15),
            confirmedAt = null,
            appUser = appUser
        )
        confirmationTokenService.saveConfirmationToken(confirmationToken)
        return token
    }

    fun enableAppUser(username: String) {
        userRepository.findByUsername(username).map {
            it.isEnabled = true
            userRepository.save(it)
        }.orElseThrow {
            UserNotFoundException("Wrong username")
        }
    }
}
