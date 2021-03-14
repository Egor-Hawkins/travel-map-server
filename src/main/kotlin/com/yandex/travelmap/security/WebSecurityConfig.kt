package com.yandex.travelmap.security

import com.yandex.travelmap.security.jwt.JWTAuthenticationFilter
import com.yandex.travelmap.security.jwt.JWTAuthorizationFilter
import com.yandex.travelmap.service.UserService
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


@Configuration
@EnableWebSecurity
class WebSecurityConfig(private val userDetailsService: UserService) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        http {
            csrf { disable() }
            cors { disable() }
            httpBasic { }
            authorizeRequests {
                authorize("/ping", permitAll)
                authorize("/api/auth/**", permitAll)
                authorize("/api/**", authenticated)
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(JWTAuthorizationFilter(authenticationManager()))
            addFilterBefore<JWTAuthorizationFilter>(JWTAuthenticationFilter(authenticationManager()))
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
        }
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder())
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}


