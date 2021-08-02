package me.pablo.streetfinder.domain.usecase

import me.pablo.streetfinder.domain.core.SearchHit
import me.pablo.streetfinder.domain.core.SearchResponse
import me.pablo.streetfinder.domain.core.Street
import me.pablo.streetfinder.domain.port.primary.StreetSearcherPort
import me.pablo.streetfinder.domain.port.secondary.Classifier
import me.pablo.streetfinder.domain.port.secondary.PreProcessor
import me.pablo.streetfinder.domain.port.secondary.Searcher
import org.springframework.stereotype.Service
import java.util.concurrent.ForkJoinPool
import kotlin.time.ExperimentalTime

@Service
class SearchStreetUseCase(
    private val classifier: Classifier,
    private val searcher: Searcher,
    private val preProcessorChain: PreProcessor
) : StreetSearcherPort {

    @OptIn(ExperimentalTime::class)
    override fun search(input: String): SearchResponse<Street> {

        val processedInput = preProcessorChain.process(input)
        val classifiedInput = classifier.classify(processedInput)
        val searchHit = searcher.search(classifiedInput)

        return SearchResponse(
            data = searchHit.street,
            classificationAccuracy = classifiedInput.avgAccuracy(),
            searchScore = searchHit.score
        )
    }

    override fun search(input: Collection<String>): Collection<SearchResponse<Street>> {

        val classifiedInputs = input
            .map { preProcessorChain.process(it) }
            .map { classifier.classify(it) }

        val searchHits = searcher.bulkSearch(classifiedInputs)

        return classifiedInputs.zip(searchHits)
            .map { (classifiedInput, searchHit) ->
                SearchResponse(
                    data = searchHit.street,
                    classificationAccuracy = classifiedInput.avgAccuracy(),
                    searchScore = searchHit.score
                )
            }
    }
}