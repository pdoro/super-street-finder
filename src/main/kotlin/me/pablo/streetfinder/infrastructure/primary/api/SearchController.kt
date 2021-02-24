package me.pablo.streetfinder.infrastructure.primary.api

import me.pablo.streetfinder.domain.port.primary.StreetSearcherPort
import me.pablo.streetfinder.infrastructure.primary.api.mapper.StreetMapper
import me.pablo.streetfinder.infrastructure.primary.api.model.ApiSearch
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.time.Instant

@RestController
class SearchController(
    private val streetSearcher: StreetSearcherPort
) {
    @GetMapping("/search")
    fun search(@RequestBody request: ApiSearch.Request): ApiSearch.Response {

        val start = Instant.now()
        val searchResult = streetSearcher.search(request.input)
        val stop = Instant.now()

        return ApiSearch.Response(
            rawInput = request.input,
            searchDuration = Duration.between(start, stop).toMillis(),
            street = StreetMapper.map(searchResult.data),
            accuracy = searchResult.classificationAccuracy,
            score = searchResult.searchScore
        )
    }
}