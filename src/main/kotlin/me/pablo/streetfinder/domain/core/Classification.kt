package me.pablo.streetfinder.domain.core

import edu.stanford.nlp.ling.CoreLabel
import label
import probability

class ClassifiedInput(private val labels: List<CoreLabel>) {

    fun get(field: String): String {
        return labels.filter {
            it.label() == field
        }.joinToString(" ") {
            it.originalText()
        }
    }

    fun avgAccuracy() = labels.map { it.probability() }.average()
}