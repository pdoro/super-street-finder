package me.pablo.streetfinder.infrastructure.secundary.search

import logger
import me.pablo.streetfinder.domain.core.ClassifiedInput
import me.pablo.streetfinder.domain.core.SearchHit
import me.pablo.streetfinder.domain.core.Street
import me.pablo.streetfinder.domain.core.StreetField
import me.pablo.streetfinder.domain.port.secondary.Searcher
import me.pablo.streetfinder.infrastructure.secundary.search.model.StreetEntity
import org.elasticsearch.index.query.QueryBuilders.*
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@Service
class ElasticClient(
    private val elasticsearchTemplate: ElasticsearchRestTemplate
): Searcher {

    private val log = logger(javaClass)
    private val STREET_INDEX = IndexCoordinates.of("streets")

    @OptIn(ExperimentalTime::class)
    override fun search(input: ClassifiedInput): SearchHit {

        val searchQuery = buildQueryFrom(input)

        val (hits, duration) = measureTimedValue {
            elasticsearchTemplate.searchOne(
                searchQuery,
                StreetEntity::class.java,
                STREET_INDEX
            )
        }

        log.info("Search time: ${duration.inMilliseconds} ms")

        val entity = hits?.content
        val street = Street(
            type = entity?.type ?: "",
            nexus = entity?.nexus ?: "",
            name = entity?.name ?: "",
            postalCode = entity?.postalCode ?: -1,
            number = entity?.number ?: -1,
            country = entity?.country ?: "",
        )

        return SearchHit(street, hits?.score ?: 0.0f)
    }

    fun buildQueryFrom(input: ClassifiedInput): NativeSearchQuery {

        val query = boolQuery()

        input.get(StreetField.TYPE) .takeIf { it.isNotBlank() }?.let {
            query.should(matchQuery(StreetField.TYPE, it).boost(0.90f))
        }
        input.get(StreetField.NAME).takeIf { it.isNotBlank() }?.let {
            query.should(matchQuery(StreetField.NAME, it).boost(2.0f))
        }
        input.get(StreetField.NEXUS).takeIf { it.isNotBlank() }?.let {
            query.should(matchQuery(StreetField.NEXUS, it).boost(0.15f))
        }
        input.get(StreetField.POSTAL_CODE).takeIf { it.isNotBlank() }?.let {
            query.should(matchQuery(StreetField.POSTAL_CODE, it))
        }
        input.get(StreetField.COUNTRY).takeIf { it.isNotBlank() }?.let {
            query.should(matchQuery(StreetField.COUNTRY, it))
        }

        return NativeSearchQueryBuilder()
            .withQuery(query)
            .build()
    }
}