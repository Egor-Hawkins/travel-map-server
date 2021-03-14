package com.yandex.travelmap.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

const val EXPIRATION_TIME = 1209600000 // 2 weeks in ms
const val AUTH_COOKIE = "AUTH_COOKIE"
val JWT_SECRET: String by lazy {
    System.getenv("JWT_SECRET") ?: "default_JWT_secret"
}

class JWTAuthenticationFilter(authenticationManager: AuthenticationManager) :
    UsernamePasswordAuthenticationFilter(authenticationManager) {
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
            .withExpiresAt(Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(Algorithm.HMAC512(JWT_SECRET))
        response.addCookie(Cookie(AUTH_COOKIE, token))
        chain.doFilter(request, response)
    }
}