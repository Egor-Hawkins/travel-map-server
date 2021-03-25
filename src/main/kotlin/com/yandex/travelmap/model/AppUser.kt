package com.yandex.travelmap.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

@Entity
@Table(name = "users")
data class AppUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(unique = true, nullable = false)
    private var username: String = "",

    @Column(nullable = false)
    private var password: String = "",

    @Column(unique = true, nullable = false)
    private var email: String = "",

    @Column(name = "non_expired", nullable = false)
    private val nonExpired: Boolean = true,
    @Column(name = "non_locked", nullable = false)
    private val nonLocked: Boolean = true,
    @Column(nullable = false)
    private val enabled: Boolean = true,
    @Column(name = "credentials_non_expired", nullable = false)
    private val credentialsNonExpired: Boolean = true,

    @ManyToMany
    @JoinTable(
        name = "city_visit",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "city_id")]
    )
    val visitedCities: MutableSet<City> = HashSet(),

    @ManyToMany
    @JoinTable(
        name = "country_visit",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "country_id")]
    )
    val visitedCountries: MutableSet<Country> = HashSet(),
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf(
        GrantedAuthority {
            "user"
        }
    )

    override fun getPassword(): String = password

    override fun getUsername(): String = username
    override fun isAccountNonExpired(): Boolean = nonExpired

    override fun isAccountNonLocked(): Boolean = nonLocked
    override fun isCredentialsNonExpired(): Boolean = credentialsNonExpired

    override fun isEnabled(): Boolean = enabled
}