package me.pablo.streetfinder.infrastructure.secundary.heuristics

import org.springframework.stereotype.Component

@Component
class StreetTypeAbbreviationHeuristic: KeywordReplacementHeuristic(
    replacements = mapOf(
        "C/"     to "Calle",
        "Avda"   to "Avenida",
        "Av"     to "Avenida",
        "Avd"    to "Avenida",
        "Plza"   to "Plaza",
        "Ctra"   to "Carretera",
        "Carret" to "Carretera",
        "Gta"    to "Glorieta",
        "Pso"    to "Paseo"
    )
)