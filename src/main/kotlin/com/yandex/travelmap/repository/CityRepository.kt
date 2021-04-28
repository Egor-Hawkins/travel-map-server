package com.yandex.travelmap.repository

import com.yandex.travelmap.model.City
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
@Transactional
interface CityRepository : JpaRepository<City, Long> {
    fun findByNameIgnoreCaseAndCountryIso(name: String, country_code: String): Optional<City>

    fun findByCountryIso(iso: String): List<City>
}