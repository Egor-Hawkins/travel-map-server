package com.yandex.travelmap.util

import com.google.gson.Gson
import com.yandex.travelmap.dto.CountryCities
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate


@Service
class CitiesRequester {
    fun getCitiesNamesByCountry(countryName: String): List<String> {
        val gson = Gson()
        val restTemplate = RestTemplate()
        val url = "https://countriesnow.space/api/v0.1/countries/cities"
        val requestJson = "{\"country\":\"$countryName\"}"
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(requestJson, headers)
        return try {
            val responseBody = restTemplate.postForObject(url, entity, String::class.java)
            val countryCitiesDTO: CountryCities = gson.fromJson(responseBody, CountryCities::class.java)
            countryCitiesDTO.data
        } catch (e: HttpClientErrorException) {
            ArrayList()
        }
    }
}