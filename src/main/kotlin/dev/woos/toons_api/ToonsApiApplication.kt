package dev.woos.toons_api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing

@EnableR2dbcAuditing
@SpringBootApplication
class ToonsApiApplication

fun main(args: Array<String>) {
    runApplication<ToonsApiApplication>(*args)
}
