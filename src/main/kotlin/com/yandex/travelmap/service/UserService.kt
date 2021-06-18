package com.yandex.travelmap.service

import com.yandex.travelmap.config.EmailConfig
import com.yandex.travelmap.dto.*
import com.yandex.travelmap.exception.CityNotFoundException
import com.yandex.travelmap.exception.CountryNotFoundException
import com.yandex.travelmap.exception.UserNotFoundException
import com.yandex.travelmap.exception.WrongUserRelationException
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
    fun getVisitedCountries(username: String, isDesire: Boolean): List<CountryResponse> {
        return userRepository.findByUsername(username).map {
            if (isDesire) {
                it.desiredCountries.map { country -> CountryResponse(country.iso, country.name) }
            } else {
                it.visitedCountries.map { country -> CountryResponse(country.iso, country.name) }
            }
        }.orElseThrow {
            UserNotFoundException("No user with name $username exists")
        }
    }

    fun addVisitedCountry(username: String, countryRequest: VisitedCountryRequest, isDesire: Boolean) {
        return userRepository.findByUsername(username).orElseThrow {
            UserNotFoundException("No user with name $username exists")
        }.let { user ->
            countryRepository.findByIso(countryRequest.iso).orElseThrow {
                CountryNotFoundException()
            }.let { country ->
                if (isDesire) {
                    user.desiredCountries.add(country)
                    country.desireers.add(user)
                } else {
                    user.visitedCountries.add(country)
                    country.visitors.add(user)
                }
                userRepository.save(user)
                countryRepository.save(country)
            }
        }
    }

    fun deleteVisitedCountry(username: String, countryRequest: VisitedCountryRequest, isDesire: Boolean) {
        return userRepository.findByUsername(username).orElseThrow {
            UserNotFoundException("No user with name $username exists")
        }.let { user ->
            countryRepository.findByIso(countryRequest.iso).orElseThrow {
                CountryNotFoundException()
            }.let { country ->
                user.visitedCountries.remove(country)
                country.visitors.remove(user)
                userRepository.save(user)
                countryRepository.save(country)
            }
        }
    }

    fun getVisitedCities(username: String, iso: String): List<CityResponse> {
        return userRepository.findByUsername(username).map {
            it.visitedCities.map { city -> CityResponse(city.country.iso, city.name) }
                .filter { response -> (iso == "" || iso == response.iso) }
                .sortedBy { response -> response.name }
        }.orElseThrow {
            UserNotFoundException("No user with name $username exists")
        }
    }

    fun addVisitedCity(username: String, cityRequest: VisitedCityRequest) {
        return userRepository.findByUsername(username).orElseThrow {
            UserNotFoundException("No user with name $username exists")
        }.let { user ->
            cityRepository.findByNameIgnoreCaseAndCountryIso(cityRequest.name, cityRequest.iso).orElseThrow {
                CityNotFoundException()
            }.let { city ->
                user.visitedCities.add(city)
                city.visitors.add(user)
                userRepository.save(user)
                cityRepository.save(city)
            }
        }
    }

    fun deleteVisitedCity(username: String, cityRequest: VisitedCityRequest) {
        return userRepository.findByUsername(username).orElseThrow {
            UserNotFoundException("No user with name $username exists")
        }.let { user ->
            cityRepository.findByNameIgnoreCaseAndCountryIso(cityRequest.name, cityRequest.iso).orElseThrow {
                CityNotFoundException()
            }.let { city ->
                user.visitedCities.remove(city)
                city.visitors.remove(user)
                userRepository.save(user)
                cityRepository.save(city)
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
            UserNotFoundException("No user with name $username exists")
        }
    }

    fun getRequestsList(username: String, myRequests: Boolean): List<String> {
        return userRepository.findByUsername(username).map { user ->
            if (myRequests) {
                user.myRequestsList.map { it.username }
            } else {
                user.requestsToMeList.map { it.username }
            }
        }.orElseThrow {
            UserNotFoundException("No user with name $username exists")
        }
    }

    fun sendFriendRequest(username: String, friendName: String) {
        val user = userRepository.findByUsername(username).orElseThrow {
            UserNotFoundException("No user with name $username exists")
        }
        val friend = userRepository.findByUsername(friendName).orElseThrow {
            UserNotFoundException("No user with name $friendName exists")
        }
        if (user.username == friend.username) {
            throw WrongUserRelationException("Can't send friend request to self")
        }
        if (user.friendsList.contains(friend)) {
            throw WrongUserRelationException("User is already a friend")
        }
        if (user.myRequestsList.contains(friend)) {
            throw WrongUserRelationException("Request already sent")
        }
        if (user.requestsToMeList.contains(friend)) {
            processFriendRequest(username, friendName, isAccept = true)
        } else {
            user.myRequestsList.add(friend)
        }
        userRepository.save(user)
        userRepository.save(friend)
    }

    fun cancelFriendRequest(username: String, friendName: String) {
        val user = userRepository.findByUsername(username).orElseThrow {
            UserNotFoundException("No user with name $username exists")
        }
        val friend = userRepository.findByUsername(friendName).orElseThrow {
            UserNotFoundException("No user with name $friendName exists")
        }
        if (user.myRequestsList.contains(friend)) {
            user.myRequestsList.remove(friend)
            userRepository.save(user)
            userRepository.save(friend)
        } else {
            throw WrongUserRelationException("No requests to user $friendName")
        }
    }

    fun processFriendRequest(username: String, friendName: String, isAccept: Boolean) {
        val user = userRepository.findByUsername(username).orElseThrow {
            UserNotFoundException("No user with name $username exists")
        }
        val friend = userRepository.findByUsername(friendName).orElseThrow {
            UserNotFoundException("No user with name $friendName exists")
        }
        if (user.requestsToMeList.contains(friend)) {
            user.requestsToMeList.remove(friend)
            if (isAccept) {
                user.friendsList.add(friend)
                friend.friendsList.add(user)
            }
            userRepository.save(user)
            userRepository.save(friend)
        } else {
            throw WrongUserRelationException("No requests from user $friendName")
        }
    }

    fun removeFriend(username: String, friendName: String) {
        val user = userRepository.findByUsername(username).orElseThrow {
            UserNotFoundException("No user with name $username exists")
        }
        val friend = userRepository.findByUsername(friendName).orElseThrow {
            UserNotFoundException("No user with name $friendName exists")
        }
        if (user.friendsList.contains(friend)) {
            user.friendsList.remove(friend)
            friend.friendsList.remove(user)
            userRepository.save(user)
            userRepository.save(friend)
        } else {
            throw WrongUserRelationException("User $friendName is not your friend")
        }
    }

    fun getFriendsList(username: String): List<String> {
        return userRepository.findByUsername(username).map {
            it.friendsList.map { friend -> friend.username }.toList()
        }.orElseThrow {
            UserNotFoundException("No user with name $username exists")
        }
    }

    fun getFriendCountries(username: String, friendName: String, isDesire: Boolean): List<CountryResponse> {
        val user = userRepository.findByUsername(username).orElseThrow {
            UserNotFoundException("No user with name $username exists")
        }
        val friend = userRepository.findByUsername(friendName).orElseThrow {
            UserNotFoundException("No user with name $friendName exists")
        }
        if (user.friendsList.contains(friend)) {
            return getVisitedCountries(friendName, isDesire)
        } else {
            throw WrongUserRelationException("User $friendName is not your friend")
        }
    }

    fun getFriendCommonCountries(username: String, friendName: String, isDesire: Boolean): List<CountryResponse> {
        val friendsCountries = getFriendCountries(username, friendName, isDesire)
        val myCountries = getVisitedCountries(username, isDesire)
        return myCountries.filter { countryResponse -> friendsCountries.contains(countryResponse) }
    }

    fun getFriendCities(username: String, friendName: String, iso: String): List<CityResponse> {
        val user = userRepository.findByUsername(username).orElseThrow {
            UserNotFoundException("No user with name $username exists")
        }
        val friend = userRepository.findByUsername(friendName).orElseThrow {
            UserNotFoundException("No user with name $friendName exists")
        }
        if (user.friendsList.contains(friend)) {
            return getVisitedCities(friendName, iso)
        } else {
            throw WrongUserRelationException("User $friendName is not your friend")
        }
    }

    fun getFriendCommonCities(username: String, friendName: String, iso: String): List<CityResponse> {
        val friendsCities = getFriendCities(username, friendName, iso)
        val myCities = getVisitedCities(username, iso)
        return myCities.filter { cityResponse -> friendsCities.contains(cityResponse) }
    }

    fun getFriendStats(username: String, friendName: String): UserStatsResponse {
        val user = userRepository.findByUsername(username).orElseThrow {
            UserNotFoundException("No user with name $username exists")
        }
        val friend = userRepository.findByUsername(friendName).orElseThrow {
            UserNotFoundException("No user with name $friendName exists")
        }
        if (user.friendsList.contains(friend)) {
            val statsResponse = getUserStats(friendName)
            statsResponse.totalCommonCities = getFriendCommonCities(username, friendName, "").size
            statsResponse.commonCountries = getFriendCommonCountries(username, friendName, isDesire = false).size
            return statsResponse
        } else {
            throw WrongUserRelationException("User $friendName is not your friend")
        }
    }

    fun getUserStats(username: String): UserStatsResponse {
        userRepository.findByUsername(username).orElseThrow {
            UserNotFoundException("No user with name $username exists")
        }.let { user ->
            val cities = user.visitedCities
            val response = UserStatsResponse(
                username = username,
                countriesNumber = user.visitedCountries.size,
                totalCitiesNumber = cities.size,
                citiesStats = LinkedList(),
                totalCommonCities = 0,
                commonCountries = 0
            )
            countryRepository.findAll().filter { country -> user.visitedCountries.contains(country) }
                .forEach { country ->
                    val citiesNumber = cities.filter { city -> city.country == country }.toList().size
                    response.citiesStats.add(
                        CitiesStatistic(
                            iso = country.iso,
                            name = country.name,
                            citiesNumber = citiesNumber
                        )
                    )
                }
            return response
        }
    }
}
