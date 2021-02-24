package me.pablo.streetfinder.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.core.io.Resource
import java.nio.file.Path

@ConstructorBinding
@ConfigurationProperties("application")
data class AppConfig(
    val modelFile: Path,
    val coreNLPProperties: Path,
    val rawDatasetFile: Path,
    val trainFile: Path
)