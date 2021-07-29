package me.pablo.streetfinder.domain.usecase

import logger
import me.pablo.streetfinder.domain.core.SearchResponse
import me.pablo.streetfinder.domain.core.Street
import me.pablo.streetfinder.domain.port.primary.StreetSearcherPort
import me.pablo.streetfinder.domain.port.secondary.Classifier
import me.pablo.streetfinder.domain.port.secondary.Searcher
import org.springframework.stereotype.Service
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@Service
class SearchStreetUseCase(
    private val classifier: Classifier,
    private val searcher: Searcher
): StreetSearcherPort {

    @OptIn(ExperimentalTime::class)
    override fun search(input: String): SearchResponse<Street> {

        val classifiedInput = classifier.classify(input)
        val searchHit = searcher.search(classifiedInput)

        return SearchResponse(
            data = searchHit.street,
            classificationAccuracy = classifiedInput.avgAccuracy(),
            searchScore = searchHit.score
        )
    }
}