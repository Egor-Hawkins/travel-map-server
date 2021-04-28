package com.yandex.travelmap.util

import com.google.gson.Gson
import com.yandex.travelmap.dto.CountryCities
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption


@Service
class CitiesRequester {
    fun getCitiesNamesByCountry(countryName: String, countryCode: String) {
        val gson = Gson()
        val restTemplate = RestTemplate()
        val url = "https://countriesnow.space/api/v0.1/countries/cities"
        val requestJson = "{\"country\":\"$countryName\"}"
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(requestJson, headers)
        try {
            val uniqueCities = HashSet<String>()
            val responseBody = restTemplate.postForObject(url, entity, String::class.java)
            val countryCitiesDTO: CountryCities = gson.fromJson(responseBody, CountryCities::class.java)
            uniqueCities.addAll(countryCitiesDTO.data)
            for (name in uniqueCities) {
                val s = "(\'$name\',\'$countryCode\'),\n"
                //println(s)
            }
            println(countryCode + ' ' + countryCitiesDTO.data.size)
        } catch (e: HttpClientErrorException) {
            println("$countryName fail")
        }
    }
}