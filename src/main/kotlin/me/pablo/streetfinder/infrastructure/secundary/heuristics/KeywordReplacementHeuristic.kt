package me.pablo.streetfinder.infrastructure.secundary.heuristics

import me.pablo.streetfinder.domain.port.secondary.PreProcessor
import org.apache.commons.lang3.StringUtils

abstract class KeywordReplacementHeuristic(
    private val replacements: Map<String, String>
): PreProcessor {

    override fun process(value: String): String {
        var retValue: String = value
        for ((key, value) in replacements) {
            retValue = StringUtils.replaceIgnoreCase(retValue, key, value)
        }
        return retValue
    }
}