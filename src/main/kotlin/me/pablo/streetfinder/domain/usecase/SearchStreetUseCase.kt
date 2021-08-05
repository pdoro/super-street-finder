package me.pablo.streetfinder.domain.usecase

import logger
import me.pablo.streetfinder.domain.core.SearchResponse
import me.pablo.streetfinder.domain.core.Street
import me.pablo.streetfinder.domain.port.primary.StreetSearcherPort
import me.pablo.streetfinder.domain.port.secondary.Classifier
import me.pablo.streetfinder.domain.port.secondary.PreProcessor
import me.pablo.streetfinder.domain.port.secondary.Searcher
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import kotlin.time.ExperimentalTime

@Service
class SearchStreetUseCase(
    private val preProcessorChain: PreProcessor,
    private val classifier: Classifier,
    private val searcher: Searcher,
) : StreetSearcherPort {

    private val log = logger(javaClass)

    @OptIn(ExperimentalTime::class)
    override fun search(input: String): SearchResponse<Street> {

        val start = Instant.now()
        val processedInput = preProcessorChain.process(input)
        val classifiedInput = classifier.classify(processedInput)
        val searchHit = searcher.search(classifiedInput)
        val stop = Instant.now()

        return SearchResponse(
            data = searchHit.street,
            classificationAccuracy = classifiedInput.avgAccuracy(),
            searchScore = searchHit.score,
            searchDuration = Duration.between(start, stop)
        )
    }

    override fun search(input: Collection<String>): Collection<SearchResponse<Street>> {

        val start = Instant.now()
        val classifiedInputs = input
            .map { preProcessorChain.process(it) }
            .map { classifier.classify(it) }

        val searchHits = searcher.bulkSearch(classifiedInputs)
        val stop = Instant.now()

        return classifiedInputs.zip(searchHits)
            .map { (classifiedInput, searchHit) ->
                SearchResponse(
                    data = searchHit.street,
                    classificationAccuracy = classifiedInput.avgAccuracy(),
                    searchScore = searchHit.score,
                    searchDuration = Duration.between(start, stop)
                )
            }
    }
}