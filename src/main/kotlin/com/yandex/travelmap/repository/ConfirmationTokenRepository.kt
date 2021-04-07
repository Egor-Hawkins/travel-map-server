package com.yandex.travelmap.repository

import com.yandex.travelmap.model.ConfirmationToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Repository
@Transactional
interface ConfirmationTokenRepository: JpaRepository<ConfirmationToken, Long> {

    fun findByToken(token: String): Optional<ConfirmationToken>
}