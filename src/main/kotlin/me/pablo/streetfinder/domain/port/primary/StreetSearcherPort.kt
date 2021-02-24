package me.pablo.streetfinder.domain.port.primary

import me.pablo.streetfinder.domain.core.SearchResponse
import me.pablo.streetfinder.domain.core.Street

interface StreetSearcherPort {
    fun search(input: String): SearchResponse<Street>
}
