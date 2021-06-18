package com.yandex.travelmap.model

import javax.persistence.*


@Entity
@Table(name = "countries")
data class Country(
    @Id
    @Column(name = "iso")
    val iso: String = "",

    @Column(name = "name", unique = true)
    val name: String = "",

    ) {
    @ManyToMany(mappedBy = "visitedCountries", cascade = [CascadeType.ALL])
    val visitors: MutableSet<AppUser> = HashSet()

    @ManyToMany(mappedBy = "desiredCountries", cascade = [CascadeType.ALL])
    val desireers: MutableSet<AppUser> = HashSet()
}
