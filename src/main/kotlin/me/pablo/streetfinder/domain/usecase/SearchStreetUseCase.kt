package me.pablo.streetfinder.domain.usecase

import me.pablo.streetfinder.domain.core.SearchResponse
import me.pablo.streetfinder.domain.core.Street
import me.pablo.streetfinder.domain.port.primary.StreetSearcherPort
import me.pablo.streetfinder.domain.port.secondary.Classifier
import me.pablo.streetfinder.domain.port.secondary.Searcher
import org.springframework.stereotype.Service

@Service
class SearchStreetUseCase(
    private val classifier: Classifier,
    private val searcher: Searcher
): StreetSearcherPort {

    override fun search(input: String): SearchResponse<Street> {

        val classifiedInput = classifier.classify(input)
        val (street, score) = searcher.search(classifiedInput)

        return SearchResponse(
            data = street,
            classificationAccuracy = classifiedInput.avgAccuracy(),
            searchScore = score
        )
    }
}