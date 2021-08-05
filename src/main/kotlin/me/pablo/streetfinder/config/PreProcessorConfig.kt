package me.pablo.streetfinder.config

import logger
import me.pablo.streetfinder.domain.port.secondary.PreProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@Configuration
class PreProcessorConfig {

    @Bean
    @Primary
    fun preProcessorChain(preprocessors: List<PreProcessor>): PreProcessor {
        return PreProcessorChain(preprocessors.sortedBy { it.order() })
    }

    class PreProcessorChain(private val preprocessors: List<PreProcessor>): PreProcessor {

        private val log = logger(javaClass)

        init {
            preprocessors.forEachIndexed { idx, preProcessor ->
                log.info("Registered ${preProcessor.javaClass.simpleName} at position $idx")
            }
        }

        @OptIn(ExperimentalTime::class)
        override fun process(value: String): String {
            val (retValue, duration) = measureTimedValue {
                preprocessors.fold(value) { acc, proc -> proc.process(acc) }
            }
            log.info("Final string: $retValue")
            log.info("PreProcessing time: ${duration.inMilliseconds} ms")
            return retValue
        }
    }
}