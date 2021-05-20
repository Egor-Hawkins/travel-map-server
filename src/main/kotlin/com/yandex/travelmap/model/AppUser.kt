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
    private var enabled: Boolean = true,
    @Column(name = "credentials_non_expired", nullable = false)
    private val credentialsNonExpired: Boolean = true,
    @Column(name = "token", nullable = true)
    private var token: String? = null,
) : UserDetails {
    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "city_visit",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "city_id")],
    )
    val visitedCities: MutableSet<City> = HashSet()

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "country_visit",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "country_code")]
    )
    val visitedCountries: MutableSet<Country> = HashSet()

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "friends",
        joinColumns = [JoinColumn(name = "first_user_id")],
        inverseJoinColumns = [JoinColumn(name = "second_user_id")]
    )
    val friendsList: MutableSet<AppUser> = HashSet()

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "requests",
        joinColumns = [JoinColumn(name = "first_user_id")],
        inverseJoinColumns = [JoinColumn(name = "second_user_id")]
    )
    val myRequestsList: MutableSet<AppUser> = HashSet()

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "requests",
        joinColumns = [JoinColumn(name = "second_user_id")],
        inverseJoinColumns = [JoinColumn(name = "first_user_id")]
    )
    val requestsToMeList: MutableSet<AppUser> = HashSet()

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
    fun setEnabled(status: Boolean) {
        this.enabled = status
    }

    fun getToken(): String? = token
    fun setToken(token: String?) {
        this.token = token
    }

    override fun toString(): String {
        val countryNames = visitedCountries.map { it.iso }
        val cityNames = visitedCities.map { it.name }
        return "AppUser(id=$id, username=$username, password=$password, visitedCountries=$countryNames,visitedCities=$cityNames"
    }
}

