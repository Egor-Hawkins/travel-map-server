package com.yandex.travelmap.controller

import com.yandex.travelmap.dto.CountryResponse
import com.yandex.travelmap.dto.VisitedCountryRequest
import com.yandex.travelmap.service.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController(private val userService: UserService) {
    @GetMapping("/{username}/visited_countries")
    fun getVisitedCountries(@PathVariable username: String): List<CountryResponse> {
        return userService.getVisitedCountries(username)
    }

    @PutMapping("/{username}/visited_countries")
    fun addVisitedCountry(@RequestBody request: VisitedCountryRequest, @PathVariable username: String) {
        return userService.addVisitedCountry(username, request)
    }

    @DeleteMapping("/{username}/visited_countries")
    fun deleteVisitedCountry(@RequestBody request: VisitedCountryRequest, @PathVariable username: String) {
        return userService.deleteVisitedCountry(username, request)
    }
}