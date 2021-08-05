package me.pablo.streetfinder.infrastructure.secundary.heuristics

import org.springframework.stereotype.Component

@Component
class StreetNexusAbbreviationHeuristic: KeywordReplacementHeuristic(
    replacements = mapOf(
        "d"  to "de",
        "dl" to "del",
    )
)