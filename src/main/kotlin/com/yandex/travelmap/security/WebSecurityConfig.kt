package com.yandex.travelmap.security

import com.yandex.travelmap.CustomConfig
import com.yandex.travelmap.security.jwt.JWTAuthenticationFilter
import com.yandex.travelmap.security.jwt.JWTAuthorizationFilter
import com.yandex.travelmap.service.UserDetailsServiceImpl
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


@Configuration
@EnableWebSecurity
class WebSecurityConfig(private val userDetailsService: UserDetailsServiceImpl) : WebSecurityConfigurerAdapter() {
    @Autowired
    val config: CustomConfig? = null

    override fun configure(http: HttpSecurity?) {
        http {
            csrf { disable() }
            cors { disable() }
            httpBasic {

            }
            authorizeRequests {
                authorize("/ping", permitAll)
                authorize("/api/auth/**", permitAll)
                authorize("/api/**", authenticated)
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(
                JWTAuthorizationFilter(
                    authenticationManager(),
                    config
                )
            )
            addFilterBefore<JWTAuthorizationFilter>(JWTAuthenticationFilter(authenticationManager(), config))
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


