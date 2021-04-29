package com.yandex.travelmap.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.yandex.travelmap.config.JWTConfig
import com.yandex.travelmap.exception.UserNotFoundException
import com.yandex.travelmap.security.service.UserDetailsServiceImpl
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.util.WebUtils
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(
    authenticationManager: AuthenticationManager,
    private val config: JWTConfig?,
    private val userService: UserDetailsServiceImpl
) :
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
        val token = cookie.value
        val username = getAuthenticationToken(token)
        val savedToken = username?.let {
            try {
                userService.findByName(it).getToken()
            } catch (e: UsernameNotFoundException) {
                null
            }
        }
        if (token == savedToken && savedToken != null) {
            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(username, null, listOf())
        }
        chain.doFilter(request, response)
    }

    private fun getAuthenticationToken(token: String): String? {

        // Parse and verify the provided token.
        return JWT.require(Algorithm.HMAC512(jwtSecret))
            .build()
            .verify(token)
            .subject ?: return null
    }
}
