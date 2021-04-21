package com.yandex.travelmap.model

import java.time.LocalDateTime
import javax.persistence.*


@Entity
@Table(name = "confirmation_tokens")
data class ConfirmationToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "token", nullable = false)
    val token: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "expires_at", nullable = false)
    val expiresAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "confirmed_at", nullable = true)
    var confirmedAt: LocalDateTime? = null,

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    val appUser: AppUser? = null
)

