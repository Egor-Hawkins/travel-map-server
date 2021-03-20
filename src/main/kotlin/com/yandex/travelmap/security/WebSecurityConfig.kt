package com.yandex.travelmap.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.yandex.travelmap.CustomConfig
import com.yandex.travelmap.security.jwt.AUTH_COOKIE
import com.yandex.travelmap.security.jwt.JWTAuthenticationFilter
import com.yandex.travelmap.security.jwt.JWTAuthorizationFilter
import com.yandex.travelmap.service.UserService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletResponse

import javax.servlet.http.HttpServletRequest
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.web.util.WebUtils


@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val userDetailsService: UserService,
) : WebSecurityConfigurerAdapter() {
    @Autowired
    val config: CustomConfig? = null

    @Bean
    fun authenticationFilter(): JWTAuthenticationFilter? {
        val authenticationFilter = JWTAuthenticationFilter(authenticationManager(), config, userDetailsService)
        authenticationFilter.setRequiresAuthenticationRequestMatcher(AntPathRequestMatcher("/login", "POST"))
        authenticationFilter.setAuthenticationManager(authenticationManagerBean())
        return authenticationFilter
    }

    override fun configure(http: HttpSecurity?) {
        http {
            csrf { disable() }
            cors { disable() }
            httpBasic {

            }
            authorizeRequests {
                authorize("/registration", permitAll)
                authorize("/api/auth/**", permitAll)
                authorize("/api/**", authenticated)
            }
            formLogin {
                loginProcessingUrl = "/login"
                authenticationSuccessHandler = AuthenticationSuccessHandler() { request: HttpServletRequest,
                                                                                response: HttpServletResponse,
                                                                                authentication: Authentication ->
                    response.status = HttpServletResponse.SC_OK
                    response.writer.println("You are logged in")

                }
                authenticationFailureHandler = AuthenticationFailureHandler() { request: HttpServletRequest?,
                                                                                response: HttpServletResponse?,
                                                                                authenticationException: AuthenticationException? ->
                    response?.status = HttpServletResponse.SC_OK
                    response?.writer?.println("Failed to log in")
                }
            }
            authenticationFilter()?.let {
                addFilterBefore<UsernamePasswordAuthenticationFilter>(
                    it
                )
            }
            addFilterBefore<JWTAuthorizationFilter>(
                JWTAuthorizationFilter(
                    authenticationManager(),
                    config,
                    userDetailsService
                )
            )
            logout {
                logoutUrl = "/logout"
                logoutSuccessHandler = LogoutSuccessHandler() { request: HttpServletRequest?,
                                                                response: HttpServletResponse?,
                                                                authentication: Authentication? ->
                    response?.writer?.println("You are logged out")
                    val cookie = request?.let { WebUtils.getCookie(it, AUTH_COOKIE) }
                    val jwtSecret: String by lazy {
                        System.getenv("JWT_SECRET") ?: config?.secret ?: "default_JWT_secret"
                    }
                    println(jwtSecret)
                    if (cookie != null && cookie.value != null && cookie.value.trim().isNotEmpty()) {
                        val token = cookie.value
                        println(token)
                        val username = JWT.require(Algorithm.HMAC512(jwtSecret))
                            .build()
                            .verify(token)
                            .subject ?: null
                        if (username != null) {
                            userDetailsService.updateToken(username, null)
                        }
                    }
                }
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
        }
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(PasswordEncoderConfig().passwordEncoder())
    }

    @Configuration
    class PasswordEncoderConfig {
        @Bean
        fun passwordEncoder(): PasswordEncoder {
            return BCryptPasswordEncoder()
        }
    }
}


