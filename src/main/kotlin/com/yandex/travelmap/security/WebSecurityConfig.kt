package com.yandex.travelmap.security

import com.yandex.travelmap.CustomConfig
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


@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val userDetailsService: UserService,
) : WebSecurityConfigurerAdapter() {
    @Autowired
    val config: CustomConfig? = null

    @Autowired
    val customAuthenticationSuccessHandler: CustomAuthenticationSuccessHandler? = null

    @Autowired
    val customAuthenticationFailureHandler: CustomAuthenticationFailureHandler? = null

    @Bean
    fun authenticationFilter(): JWTAuthenticationFilter? {
        val authenticationFilter = JWTAuthenticationFilter(authenticationManager(), config)
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
                authenticationSuccessHandler = customAuthenticationSuccessHandler
                authenticationFailureHandler = customAuthenticationFailureHandler
            }

            authenticationFilter()?.let {
                addFilterBefore<UsernamePasswordAuthenticationFilter>(
                    it
                )
            }
            addFilterBefore<JWTAuthorizationFilter>(
                JWTAuthorizationFilter(
                    authenticationManager(),
                    config
                )
            )
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


    //Idk why it doesn't work without this, Ill fix soon
    @Component
    class CustomAuthenticationSuccessHandler : AuthenticationSuccessHandler {
        override fun onAuthenticationSuccess(
            request: HttpServletRequest,
            response: HttpServletResponse, authentication: Authentication
        ) {
            response.status = HttpServletResponse.SC_OK
        }
    }

    @Component
    class CustomAuthenticationFailureHandler : AuthenticationFailureHandler {
        override fun onAuthenticationFailure(
            request: HttpServletRequest?,
            response: HttpServletResponse?,
            exception: AuthenticationException?
        ) {
            response?.status = HttpServletResponse.SC_OK
        }
    }
}


