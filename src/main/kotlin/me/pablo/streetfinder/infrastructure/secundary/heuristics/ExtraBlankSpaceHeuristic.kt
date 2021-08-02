package me.pablo.streetfinder.infrastructure.secundary.heuristics

import me.pablo.streetfinder.domain.port.secondary.PreProcessor
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component

@Component
class ExtraBlankSpaceHeuristic: PreProcessor {
    override fun process(value: String): String = StringUtils.normalizeSpace(value)
}