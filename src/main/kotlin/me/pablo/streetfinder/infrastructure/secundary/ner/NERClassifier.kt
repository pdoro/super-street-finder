package me.pablo.streetfinder.infrastructure.secundary.ner

import edu.stanford.nlp.ie.crf.CRFClassifier
import edu.stanford.nlp.ling.CoreLabel
import label
import logger
import me.pablo.streetfinder.config.AppConfig
import me.pablo.streetfinder.domain.core.ClassifiedInput
import me.pablo.streetfinder.domain.port.secondary.Classifier
import org.springframework.stereotype.Service
import probability
import java.nio.file.Files
import java.time.Duration
import java.time.Instant
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@Service
class NERClassifier(
    private val appConfig: AppConfig
): Classifier {

    private val crfClassifier: CRFClassifier<CoreLabel> by lazy {
        CRFClassifier.getClassifier(Files.newInputStream(appConfig.modelFile))
    }
    private val log = logger(javaClass)

    @OptIn(ExperimentalTime::class)
    override fun classify(input: String): ClassifiedInput {

        val (classification, duration) = measureTimedValue { crfClassifier.classify(input) }

        log.info("Classification time: ${duration.inMilliseconds} ms")

        classification.flatten()
            .forEach {
                log.info("${it.originalText()} - ${it.label()} - prob: ${it.probability()}")
            }

        return ClassifiedInput(classification.flatten())
    }
}