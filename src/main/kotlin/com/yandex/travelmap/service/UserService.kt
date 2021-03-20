package com.yandex.travelmap.service

import com.yandex.travelmap.model.AppUser
import com.yandex.travelmap.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.lang.IllegalStateException

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username) ?: throw UsernameNotFoundException(username)
        return User(user.username, user.password, listOf())
    }

    fun registerUser(appUser: AppUser): String {
        val emailExists = (appUser.email?.let { userRepository.findByEmail(it) } != null)
        if (emailExists) {
            return "User with his email already exists"
        }
        val usernameExists = (appUser.username?.let { userRepository.findByUsername(it) } != null)
        if (usernameExists) {
            return "User with his name already exists"
        }
        val encodedPassword: String = passwordEncoder.encode(appUser.password)
        appUser.password = encodedPassword
        appUser.enabled = true //TODO send confirmation?
        userRepository.save(appUser)
        return "Registered successfully"
    }

    fun findByName(username: String): AppUser? {
        return userRepository.findByUsername(username)
    }

    fun updateToken(username: String, token: String) {
        val appUser: AppUser? = userRepository.findByUsername(username)
        if (appUser != null) {
            appUser.token = token
            userRepository.save(appUser)
        }
    }
}