package me.pablo.streetfinder.infrastructure.primary.api

import com.fasterxml.jackson.databind.ObjectMapper
import me.pablo.streetfinder.domain.port.primary.StreetSearcherPort
import me.pablo.streetfinder.infrastructure.primary.api.mapper.StreetMapper
import me.pablo.streetfinder.infrastructure.primary.api.model.ApiSearch
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.time.Duration
import java.time.Instant

@RestController
class SearchController(
    private val streetSearcher: StreetSearcherPort,
    private val objectMapper: ObjectMapper
): TextWebSocketHandler() {

    @GetMapping("/search")
    fun search(
        @RequestBody request: ApiSearch.Request
    ): ApiSearch.Response {

        val start = Instant.now()
        val searchResult = streetSearcher.search(request.input)
        val stop = Instant.now()

        return ApiSearch.Response(
            rawInput = request.input,
            searchDurationMillis = Duration.between(start, stop).toMillis(),
            street = StreetMapper.map(searchResult.data),
            accuracy = searchResult.classificationAccuracy,
            score = searchResult.searchScore
        )
    }

    @GetMapping("/search/bulk")
    fun search(
        @RequestBody bulkRequest: List<ApiSearch.Request>
    ): List<ApiSearch.Response> {

        val start = Instant.now()
        val searchResults = streetSearcher.search(bulkRequest.map { it.input })
        val stop = Instant.now()

        return bulkRequest.zip(searchResults)
            .map { (request, searchResult) ->
                ApiSearch.Response(
                    rawInput = request.input,
                    searchDurationMillis = Duration.between(start, stop).toMillis(),
                    street = StreetMapper.map(searchResult.data),
                    accuracy = searchResult.classificationAccuracy,
                    score = searchResult.searchScore
            )
        }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val payload = message.payload
        val searchResponse = search(ApiSearch.Request(payload))
        session.sendMessage(TextMessage(objectMapper.writeValueAsString(searchResponse)))
    }
}