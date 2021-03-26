package com.yandex.travelmap.controller

import com.yandex.travelmap.dto.CountryResponse
import com.yandex.travelmap.dto.VisitedCountryRequest
import com.yandex.travelmap.exception.NotAuthorizedException
import com.yandex.travelmap.service.UserService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController(private val userService: UserService) {

    private fun getCurrentUser(): UserDetails {
        return SecurityContextHolder.getContext().authentication?.principal as? UserDetails
            ?: throw NotAuthorizedException()
    }

    @GetMapping("/visited_countries")
    fun getVisitedCountries(): List<CountryResponse> {
        return userService.getVisitedCountries(getCurrentUser().username)
    }

    @PutMapping("/visited_countries")
    fun addVisitedCountry(@RequestBody request: VisitedCountryRequest) {
        return userService.addVisitedCountry(getCurrentUser().username, request)
    }

    @DeleteMapping("/visited_countries")
    fun deleteVisitedCountry(@RequestBody request: VisitedCountryRequest) {
        return userService.deleteVisitedCountry(getCurrentUser().username, request)
    }
}