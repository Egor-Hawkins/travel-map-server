package com.yandex.travelmap.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.yandex.travelmap.config.JWTConfig
import com.yandex.travelmap.security.jwt.AUTH_COOKIE
import com.yandex.travelmap.security.jwt.JWTAuthenticationFilter
import com.yandex.travelmap.security.jwt.JWTAuthorizationFilter
import com.yandex.travelmap.security.service.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.util.WebUtils
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val userDetailsService: UserDetailsServiceImpl,
) : WebSecurityConfigurerAdapter() {
    @Autowired
    val config: JWTConfig? = null

    @Autowired
    val passwordEncoderConfig: PasswordEncoderConfig? = null

    @Bean
    fun authenticationFilter(): JWTAuthenticationFilter? {
        val authenticationFilter = JWTAuthenticationFilter(authenticationManager(), config, userDetailsService)
        authenticationFilter.setRequiresAuthenticationRequestMatcher(AntPathRequestMatcher("/login", "POST"))
        authenticationFilter.setAuthenticationManager(authenticationManagerBean())
        return authenticationFilter
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource? {
        val configuration = CorsConfiguration()
        val origin = System.getenv("SITE_URL") ?: "http://localhost:3000"
        configuration.allowedOrigins = listOf(origin)
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("authorization", "content-type", "x-auth-token")
        configuration.exposedHeaders = listOf("x-auth-token")
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    override fun configure(http: HttpSecurity?) {
        http {
            csrf { disable() }
            cors { }
            authorizeRequests {
                authorize("/registration/confirm", permitAll)
                authorize("/registration", permitAll)
                authorize("/health", permitAll)
                authorize("/api/auth/**", permitAll)
                authorize("/api/cities", permitAll)
                authorize("/api/**", authenticated)
            }
            exceptionHandling {
                authenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
            }
            formLogin {
                loginProcessingUrl = "/login"
                authenticationSuccessHandler = AuthenticationSuccessHandler { request: HttpServletRequest,
                                                                              response: HttpServletResponse,
                                                                              authentication: Authentication ->
                    response.status = HttpServletResponse.SC_OK
                    response.writer.println("You are logged in")

                }
                authenticationFailureHandler = AuthenticationFailureHandler { request: HttpServletRequest?,
                                                                              response: HttpServletResponse?,
                                                                              authenticationException: AuthenticationException? ->
                    response?.status = HttpServletResponse.SC_UNAUTHORIZED
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
                logoutSuccessHandler = LogoutSuccessHandler { request: HttpServletRequest?,
                                                              response: HttpServletResponse?,
                                                              authentication: Authentication? ->
                    response?.writer?.println("You are logged out")
                    val cookie = request?.let { WebUtils.getCookie(it, AUTH_COOKIE) }
                    val jwtSecret: String by lazy {
                        System.getenv("JWT_SECRET") ?: config?.secret ?: "default_JWT_secret"
                    }
                    if (cookie != null && cookie.value != null && cookie.value.trim().isNotEmpty()) {
                        val token = cookie.value
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
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoderConfig?.passwordEncoder())
    }

    @Configuration
    class PasswordEncoderConfig {
        @Bean
        fun passwordEncoder(): PasswordEncoder {
            return BCryptPasswordEncoder()
        }
    }
}
