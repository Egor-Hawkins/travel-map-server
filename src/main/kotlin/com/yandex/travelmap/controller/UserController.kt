package com.yandex.travelmap.controller

import com.yandex.travelmap.dto.*
import com.yandex.travelmap.exception.NotAuthorizedException
import com.yandex.travelmap.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/user")
class UserController(private val userService: UserService) {

    private fun getCurrentUsername(): String {
        return SecurityContextHolder.getContext().authentication?.principal?.toString()
            ?: throw NotAuthorizedException()
    }

    @GetMapping("/stats")
    fun getStats(): UserStatsResponse {
        return userService.getUserStats(getCurrentUsername())
    }

    @GetMapping("/visited_countries")
    fun getVisitedCountries(): List<CountryResponse> {
        return userService.getVisitedCountries(getCurrentUsername(), false)
    }

    @PutMapping("/visited_countries")
    fun addVisitedCountry(@RequestBody request: VisitedCountryRequest) {
        return userService.addVisitedCountry(getCurrentUsername(), request, false)
    }

    @DeleteMapping("/visited_countries")
    fun deleteVisitedCountry(@RequestBody request: VisitedCountryRequest) {
        return userService.deleteVisitedCountry(getCurrentUsername(), request, false)
    }

    @GetMapping("/desired_countries")
    fun getDesiredCountries(): List<CountryResponse> {
        return userService.getVisitedCountries(getCurrentUsername(), true)
    }

    @PutMapping("/desired_countries")
    fun addDesiredCountry(@RequestBody request: VisitedCountryRequest) {
        return userService.addVisitedCountry(getCurrentUsername(), request, true)
    }

    @DeleteMapping("/desired_countries")
    fun deleteDesiredCountry(@RequestBody request: VisitedCountryRequest) {
        return userService.deleteVisitedCountry(getCurrentUsername(), request, true)
    }

    @PostMapping("/visited_cities")
    fun getVisitedCities(@RequestBody request: CitiesByCountryListRequest): List<CityResponse> {
        return userService.getVisitedCities(getCurrentUsername(), request.iso)
    }

    @PutMapping("/visited_cities")
    fun addVisitedCities(@RequestBody request: VisitedCityRequest) {
        return userService.addVisitedCity(getCurrentUsername(), request)
    }

    @DeleteMapping("/visited_cities")
    fun deleteVisitedCities(@RequestBody request: VisitedCityRequest) {
        return userService.deleteVisitedCity(getCurrentUsername(), request)
    }

    @PostMapping("/friends/remove")
    fun removeFromFriends(@RequestBody request: FriendRequest): ResponseEntity<Any> {
        return try {
            userService.removeFriend(getCurrentUsername(), request.friendName)
            ResponseEntity(HttpStatus.OK)
        } catch (e: ResponseStatusException) {
            ResponseEntity(e.reason, e.status)
        }
    }

    @PostMapping("/friends/request")
    fun getRequestsList(@RequestBody request: FriendRequestsRequest): List<String> {
        return userService.getRequestsList(getCurrentUsername(), request.myRequests)
    }

    @PostMapping("/friends/request/send")
    fun sendFriendRequest(@RequestBody request: FriendRequest): ResponseEntity<Any> {
        return try {
            userService.sendFriendRequest(getCurrentUsername(), request.friendName)
            ResponseEntity(HttpStatus.OK)
        } catch (e: ResponseStatusException) {
            ResponseEntity(e.reason, e.status)
        }
    }

    @PostMapping("/friends/request/cancel")
    fun cancelFriendRequest(@RequestBody request: FriendRequest): ResponseEntity<Any> {
        return try {
            userService.cancelFriendRequest(getCurrentUsername(), request.friendName)
            ResponseEntity(HttpStatus.OK)
        } catch (e: ResponseStatusException) {
            ResponseEntity(e.reason, e.status)
        }
    }

    @PostMapping("/friends/request/accept")
    fun acceptFriendRequest(@RequestBody request: FriendRequest): ResponseEntity<Any> {
        return try {
            userService.processFriendRequest(getCurrentUsername(), request.friendName, isAccept = true)
            ResponseEntity(HttpStatus.OK)
        } catch (e: ResponseStatusException) {
            ResponseEntity(e.reason, e.status)
        }
    }

    @PostMapping("/friends/request/decline")
    fun declineFriendRequest(@RequestBody request: FriendRequest): ResponseEntity<Any> {
        return try {
            userService.processFriendRequest(getCurrentUsername(), request.friendName, isAccept = false)
            ResponseEntity(HttpStatus.OK)
        } catch (e: ResponseStatusException) {
            ResponseEntity(e.reason, e.status)
        }
    }

    @GetMapping("/friends")
    fun getFriendsList(): List<String> {
        return userService.getFriendsList(getCurrentUsername())
    }

    @PostMapping("/friends/countries")
    fun getFriendCountries(@RequestBody request: FriendRequest): ResponseEntity<Any> {
        return try {
            ResponseEntity(
                userService.getFriendCountries(getCurrentUsername(), request.friendName, false),
                HttpStatus.OK
            )
        } catch (e: ResponseStatusException) {
            ResponseEntity(e.reason, e.status)
        }
    }

    @PostMapping("/friends/countries/desired")
    fun getFriendDesiredCountries(@RequestBody request: FriendRequest): ResponseEntity<Any> {
        return try {
            ResponseEntity(
                userService.getFriendCountries(getCurrentUsername(), request.friendName, true),
                HttpStatus.OK
            )
        } catch (e: ResponseStatusException) {
            ResponseEntity(e.reason, e.status)
        }
    }

    @PostMapping("/friends/countries/common")
    fun getFriendCommonCountries(@RequestBody request: FriendRequest): ResponseEntity<Any> {
        return try {
            ResponseEntity(
                userService.getFriendCommonCountries(getCurrentUsername(), request.friendName, false),
                HttpStatus.OK
            )
        } catch (e: ResponseStatusException) {
            ResponseEntity(e.reason, e.status)
        }
    }

    @PostMapping("/friends/countries/common/desired")
    fun getFriendCommonDesiredCountries(@RequestBody request: FriendRequest): ResponseEntity<Any> {
        return try {
            ResponseEntity(
                userService.getFriendCommonCountries(getCurrentUsername(), request.friendName, true),
                HttpStatus.OK
            )
        } catch (e: ResponseStatusException) {
            ResponseEntity(e.reason, e.status)
        }
    }


    @PostMapping("/friends/cities")
    fun getFriendCities(@RequestBody request: FriendCitiesRequest): ResponseEntity<Any> {
        return try {
            ResponseEntity(
                userService.getFriendCities(getCurrentUsername(), request.friendName, request.iso),
                HttpStatus.OK
            )
        } catch (e: ResponseStatusException) {
            ResponseEntity(e.reason, e.status)
        }
    }

    @PostMapping("/friends/cities/common")
    fun getFriendCommonCities(@RequestBody request: FriendCitiesRequest): ResponseEntity<Any> {
        return try {
            ResponseEntity(
                userService.getFriendCommonCities(getCurrentUsername(), request.friendName, request.iso),
                HttpStatus.OK
            )
        } catch (e: ResponseStatusException) {
            ResponseEntity(e.reason, e.status)
        }
    }

    @PostMapping("/friends/stats")
    fun getFriendStats(@RequestBody request: FriendRequest): ResponseEntity<Any> {
        return try {
            ResponseEntity(
                userService.getFriendStats(getCurrentUsername(), request.friendName),
                HttpStatus.OK
            )
        } catch (e: ResponseStatusException) {
            ResponseEntity(e.reason, e.status)
        }
    }
}
