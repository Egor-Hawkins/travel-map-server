package com.yandex.travelmap.service

import com.yandex.travelmap.model.AppUser
import com.yandex.travelmap.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = findByName(username)
        return User(user.username, user.password, listOf())
    }

    fun registerUser(appUser: AppUser): Boolean {
        val emailExists = (appUser.email?.let { userRepository.findByEmail(it).isPresent })
        if (emailExists == true) {
            throw IllegalStateException("User with this email already exists")
        }
        val usernameExists = (appUser.username?.let { userRepository.findByUsername(it).isPresent })
        if (usernameExists == true) {
            throw IllegalStateException("User with this name already exists")
        }
        val encodedPassword: String = passwordEncoder.encode(appUser.password)
        appUser.password = encodedPassword
        appUser.enabled = true //TODO send confirmation?
        userRepository.save(appUser)
        return true
    }

    fun findByName(username: String): AppUser {
        return userRepository.findByUsername(username)
            .orElseThrow { throw UsernameNotFoundException("No user with name $username") }
    }

    fun updateToken(username: String, token: String?) {
        val appUser: AppUser = findByName(username)
        appUser.token = token
        userRepository.save(appUser)
    }
}