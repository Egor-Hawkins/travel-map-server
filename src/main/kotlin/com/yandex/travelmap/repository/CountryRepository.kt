package com.yandex.travelmap.repository

import com.yandex.travelmap.model.Country
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
@Transactional
interface CountryRepository : JpaRepository<Country, Long> {
    fun findByIso(iso: String): Optional<Country>
}