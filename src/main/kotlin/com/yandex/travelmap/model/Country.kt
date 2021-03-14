package com.yandex.travelmap.model

import javax.persistence.*


@Entity
@Table(name = "countries")
data class Country(
    @Id
    @Column(name = "iso")
    val iso: String = "",

    @Column(name = "name")
    val name: String = ""
)
