package me.pablo.streetfinder.infrastructure.secundary.search

import logger
import me.pablo.streetfinder.domain.core.ClassifiedInput
import me.pablo.streetfinder.domain.core.SearchHit
import me.pablo.streetfinder.domain.core.Street
import me.pablo.streetfinder.domain.core.StreetField
import me.pablo.streetfinder.domain.port.secondary.Searcher
import me.pablo.streetfinder.infrastructure.secundary.search.model.StreetEntity
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
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

    @OptIn(ExperimentalTime::class)
    override fun bulkSearch(inputs: Collection<ClassifiedInput>): Collection<SearchHit> {

        val searchQueries = inputs.map { buildQueryFrom(it) }

        val (hits, duration) = measureTimedValue {
            elasticsearchTemplate.multiSearch(
                searchQueries,
                StreetEntity::class.java,
                STREET_INDEX
            )
        }

        log.info("Search time: ${duration.inMilliseconds} ms")

        return hits.map { it.searchHits[0] }
            .map {
                SearchHit(
                    Street(
                        type = it.content.type,
                        nexus = it.content.nexus,
                        name = it.content.name,
                        postalCode = it.content.postalCode,
                        number = it.content.number,
                        country = it.content.country,
                    ),
                    score = it.score
                )
            }
    }

    fun buildQueryFrom(input: ClassifiedInput): NativeSearchQuery {

        val query = boolQuery()

        input.ifContains(StreetField.TYPE) {
            query.should(matchQuery(StreetField.TYPE, it).boost(0.90f))
        }
        input.ifContains(StreetField.NAME) {
            query.should(matchQuery(StreetField.NAME, it).boost(2.0f))
        }
        input.ifContains(StreetField.NEXUS) {
            query.should(matchQuery(StreetField.NEXUS, it).boost(0.15f))
        }
        input.ifContains(StreetField.POSTAL_CODE) {
            query.should(matchQuery(StreetField.POSTAL_CODE, it))
        }
        input.ifContains(StreetField.COUNTRY) {
            query.should(matchQuery(StreetField.COUNTRY, it))
        }

        return NativeSearchQueryBuilder()
            .withQuery(query)
            .withPageable(PageRequest.of(0, 1))
            .build()
    }

    fun ClassifiedInput.ifContains(field: String, query: (String) -> Unit) {
        this.get(field) .takeIf { it.isNotBlank() }?.let { query.invoke(it) }
    }
}