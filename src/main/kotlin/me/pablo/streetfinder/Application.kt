package me.pablo.streetfinder

import me.pablo.streetfinder.config.AppConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties(value = [
    AppConfig::class
])
@SpringBootApplication
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}