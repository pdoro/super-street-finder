package me.pablo.streetfinder.infrastructure.secundary.heuristics

import me.pablo.streetfinder.domain.port.secondary.PreProcessor
import org.ahocorasick.trie.Trie
import org.apache.commons.lang3.StringUtils

abstract class KeywordReplacementHeuristic(
    private val replacements: Map<String, String>
): PreProcessor {

    private val ahoCorasick: Trie = Trie.builder()
        .ignoreCase()
        .onlyWholeWords()
        .addKeywords(replacements.keys)
        .build()

    override fun process(value: String): String {

        val matches = ahoCorasick.parseText(value)

        // Optimization - return if no text matches to avoid StringBuilder allocation
        if(matches.isEmpty()) {
            return value
        }

        val sb = StringBuilder(value)
        var displacement = 0

        for (match in matches) {
            val replacement = replacements[match.keyword]

            val start = match.start + displacement
            val end = match.end + displacement + 1
            sb.replace(start, end, replacement)

            /* Compensate match locations for previous substitutions */
            displacement += replacement?.length?.minus(match.keyword.length) ?: 0
        }

        return sb.toString()

        /* Dead simple solution to replacement. Creates a new string and SB every time */

//        var retValue: String = value
//        for ((key, value) in replacements) {
//            retValue = StringUtils.replaceIgnoreCase(retValue, key, value)
//        }
//        return retValue

    }
}