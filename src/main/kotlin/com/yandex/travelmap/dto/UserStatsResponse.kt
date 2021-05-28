package com.yandex.travelmap.dto

data class UserStatsResponse(
    val username: String,
    val countriesNumber: Int,
    val totalCitiesNumber: Int,
    var commonCountries: Int,
    var totalCommonCities: Int,
    val citiesStats: MutableList<CitiesStatistic>
)

data class CitiesStatistic(val iso: String, val name: String, val citiesNumber: Int)
