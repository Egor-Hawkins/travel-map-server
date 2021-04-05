package com.yandex.travelmap.model

import javax.persistence.*

@Entity
@Table(name = "cities")
data class City(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column
    val name: String = "",

    @Column
    val latitude: Double = 0.0,

    @Column
    val longitude: Double = 0.0
) {

    @ManyToOne
    @JoinColumn(name = "country_code", referencedColumnName = "iso")
    val country: Country = Country()

    @ManyToMany(mappedBy = "visitedCities", cascade = [CascadeType.ALL])
    val visitors: MutableSet<AppUser> = HashSet()
}