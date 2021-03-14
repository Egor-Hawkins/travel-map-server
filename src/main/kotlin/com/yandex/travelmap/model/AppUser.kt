package com.yandex.travelmap.model

import javax.persistence.*

@Entity
@Table(name = "users")
class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(unique = true)
    var username: String? = null
    var password: String? = null

    @Column(unique = true)
    var email: String? = null

    var enabled: Boolean? = null
}