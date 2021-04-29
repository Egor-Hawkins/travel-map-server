package com.yandex.travelmap.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import javax.sql.DataSource

@Configuration
@ConfigurationProperties(prefix = "datasource", ignoreUnknownFields = true)
@PropertySource(value = ["classpath:custom-props.properties"])
class DataSourceConfig {
    var testing: Boolean = true
    var url: String? = null
    var driverClassName: String? = null
    var username: String? = null
    var password: String? = null

    @Bean
    fun getDataSource(): DataSource {
        val dataSourceBuilder = DataSourceBuilder.create()
        if (testing) {
            dataSourceBuilder.url("jdbc:postgresql://localhost:5432/travel_map_db?createDatabaseIfNotExist=true&currentSchema=public&allowPublicKeyRetrieval=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC")
            dataSourceBuilder.driverClassName("org.postgresql.Driver")
            dataSourceBuilder.username("postgres")
            dataSourceBuilder.password("postgres")
        } else {
            dataSourceBuilder.url(url!!)
            dataSourceBuilder.driverClassName(driverClassName!!)
            dataSourceBuilder.username(username)
            dataSourceBuilder.password(password)
        }
        return dataSourceBuilder.build()
    }
}