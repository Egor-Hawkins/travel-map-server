package com.yandex.travelmap.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.yandex.travelmap.CustomConfig
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.util.WebUtils
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(authenticationManager: AuthenticationManager, private val config: CustomConfig?) :
    BasicAuthenticationFilter(authenticationManager) {
    private val jwtSecret: String by lazy {
        System.getenv("JWT_SECRET") ?: config?.secret ?: "default_JWT_secret"
    }

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val cookie = WebUtils.getCookie(request, AUTH_COOKIE)
        if (cookie == null || cookie.value == null || cookie.value.trim().isEmpty()) {
            // If there is no cookie, the user is not authenticated. Continue the filter chain.
            chain.doFilter(request, response)
            return
        }
        val authenticationToken = getAuthenticationToken(cookie.value)
        SecurityContextHolder.getContext().authentication = authenticationToken
        chain.doFilter(request, response)
    }

    private fun getAuthenticationToken(token: String): UsernamePasswordAuthenticationToken? {

        // Parse and verify the provided token.
        val parsedToken = JWT.require(Algorithm.HMAC512(jwtSecret))
            .build()
            .verify(token)
            .subject ?: return null

        return UsernamePasswordAuthenticationToken(parsedToken, null, listOf())
    }
}