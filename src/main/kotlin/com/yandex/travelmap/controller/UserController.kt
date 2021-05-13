package com.yandex.travelmap.controller

import com.yandex.travelmap.dto.*
import com.yandex.travelmap.exception.NotAuthorizedException
import com.yandex.travelmap.service.UserService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController(private val userService: UserService) {

    private fun getCurrentUsername(): String {
        return SecurityContextHolder.getContext().authentication?.principal?.toString()
            ?: throw NotAuthorizedException()
    }

    @GetMapping("/visited_countries")
    fun getVisitedCountries(): List<CountryResponse> {
        return userService.getVisitedCountries(getCurrentUsername())
    }

    @PutMapping("/visited_countries")
    fun addVisitedCountry(@RequestBody request: VisitedCountryRequest) {
        return userService.addVisitedCountry(getCurrentUsername(), request)
    }

    @DeleteMapping("/visited_countries")
    fun deleteVisitedCountry(@RequestBody request: VisitedCountryRequest) {
        return userService.deleteVisitedCountry(getCurrentUsername(), request)
    }

    @GetMapping("/visited_cities")
    fun getVisitedCities(@RequestBody request: CitiesByCountryListRequest): List<CityResponse> {
        return userService.getVisitedCities(getCurrentUsername(), request)
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
    fun removeFromFriends(@RequestBody request: FriendRequest) {
        return userService.removeFriend(getCurrentUsername(), request.friendName)
    }

    @GetMapping("/friends/request")
    fun getRequestsList(@RequestBody request: FriendRequestsRequest): List<String> {
        return userService.getRequestsList(getCurrentUsername(), request.myRequests)
    }

    @PostMapping("/friends/request/send")
    fun sendFriendRequest(@RequestBody request: FriendRequest) {
        return userService.sendFriendRequest(getCurrentUsername(), request.friendName)
    }

    @PostMapping("/friends/request/cancel")
    fun cancelFriendRequest(@RequestBody request: FriendRequest) {
        return userService.cancelFriendRequest(getCurrentUsername(), request.friendName)
    }

    @PostMapping("/friends/request/accept")
    fun acceptFriendRequest(@RequestBody request: FriendRequest) {
        return userService.processFriendRequest(getCurrentUsername(), request.friendName, isAccept = true)
    }

    @PostMapping("/friends/request/decline")
    fun declineFriendRequest(@RequestBody request: FriendRequest) {
        return userService.processFriendRequest(getCurrentUsername(), request.friendName, isAccept = false)
    }

    @GetMapping("/friends")
    fun getFriendsList(): List<String> {
        return userService.getFriendsList(getCurrentUsername())
    }

    @PostMapping("/friends")
    fun getFriendData(@RequestBody request: FriendRequest): List<CountryResponse> { // TODO more info (cities, his info, friends, etc)
        return userService.getFriendData(getCurrentUsername(), request.friendName)
    }
}
