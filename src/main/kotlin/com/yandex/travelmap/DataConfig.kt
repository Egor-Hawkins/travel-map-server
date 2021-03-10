package com.yandex.travelmap

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.io.File
import java.util.*
import javax.sql.DataSource
import kotlin.io.path.Path

data class Config(
    val dbName: String,
    val dbUsername: String,
    val dbPassword: String
)

@Configuration
class DataConfig {
    @Bean
    fun dataSource(): DataSource {
        val driver = DriverManagerDataSource()
        driver.setDriverClassName("org.postgresql.Driver")
        val mapper = ObjectMapper(YAMLFactory())
        mapper.findAndRegisterModules()
        val config = mapper.readValue<Config>(File("application.yml"))
        driver.url = "jdbc:postgresql:${config.dbName}"
        driver.username = config.dbUsername
        driver.password = config.dbPassword
        return driver
    }
}