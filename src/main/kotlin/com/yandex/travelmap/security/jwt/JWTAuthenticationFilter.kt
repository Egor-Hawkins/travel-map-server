package com.yandex.travelmap.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.yandex.travelmap.CustomConfig
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

const val AUTH_COOKIE = "AUTH_COOKIE"


class JWTAuthenticationFilter(authenticationManager: AuthenticationManager, private val config: CustomConfig?) :
    UsernamePasswordAuthenticationFilter(authenticationManager) {
    private val jwtSecret: String by lazy {
        System.getenv("JWT_SECRET") ?: config?.secret ?: "default_JWT_secret"
    }
    private val expirationTime = config?.expirationTime ?: 1209600000 // 2 weeks in ms

    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication?
    ) {
        val user =
            authResult?.principal as? User ?: throw IllegalArgumentException("authResult must be an instance of User")
        val token = JWT.create()
            .withSubject(user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + expirationTime))
            .sign(Algorithm.HMAC512(jwtSecret))
        response.addCookie(Cookie(AUTH_COOKIE, token))
        chain.doFilter(request, response)
    }
}