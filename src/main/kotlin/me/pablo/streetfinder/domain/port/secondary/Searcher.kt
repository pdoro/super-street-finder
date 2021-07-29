package me.pablo.streetfinder.domain.port.secondary

import me.pablo.streetfinder.domain.core.ClassifiedInput
import me.pablo.streetfinder.domain.core.SearchHit

interface Searcher {
    fun search(input: ClassifiedInput): SearchHit
}
