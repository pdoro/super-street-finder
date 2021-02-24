package me.pablo.streetfinder.infrastructure.secundary.ner

import edu.stanford.nlp.ie.crf.CRFClassifier
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.sequences.SeqClassifierFlags
import edu.stanford.nlp.util.StringUtils
import me.pablo.streetfinder.config.AppConfig
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.util.*
import kotlin.io.path.absolutePathString
import kotlin.io.path.name

@Service
class NERTrainer(
    private val appConfig: AppConfig
) {
    fun train() {
        val props: Properties = StringUtils.propFileToProperties(appConfig.coreNLPProperties.toAbsolutePath().toString())
        props["trainFile"] = appConfig.trainFile.toString()
        props["serializeTo"] = appConfig.modelFile.toString()

        val flags = SeqClassifierFlags(props)
        val crf: CRFClassifier<CoreLabel> = CRFClassifier(flags)
        crf.train()
        crf.serializeClassifier(appConfig.modelFile.toString())
    }

    fun modelExists(): Boolean = Files.exists(appConfig.modelFile)
}