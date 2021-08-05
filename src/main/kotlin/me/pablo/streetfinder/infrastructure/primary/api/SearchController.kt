package me.pablo.streetfinder.infrastructure.primary.api

import com.fasterxml.jackson.databind.ObjectMapper
import me.pablo.streetfinder.domain.port.primary.StreetSearcherPort
import me.pablo.streetfinder.infrastructure.primary.api.mapper.ApiSearchMapper
import me.pablo.streetfinder.infrastructure.primary.api.mapper.StreetMapper
import me.pablo.streetfinder.infrastructure.primary.api.model.ApiSearch
import me.pablo.streetfinder.infrastructure.primary.api.model.Batch
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
        @RequestBody searchRequest: ApiSearch.Request
    ): ApiSearch.Response {

        val searchResult = streetSearcher.search(searchRequest.input)
        return ApiSearchMapper.toResponse(searchRequest, searchResult)
    }

    @GetMapping("/search/bulk")
    fun search(
        @RequestBody bulkRequest: Batch.Request<ApiSearch.Request>
    ): Batch.Response<ApiSearch.Response> {

        val searchResults = streetSearcher.search(bulkRequest.list.map { it.input })

        val responseList = bulkRequest.list.zip(searchResults)
            .map { (searchRequest, searchResult) -> ApiSearchMapper.toResponse(searchRequest, searchResult) }

        val totalDuration = responseList.map { it.searchDurationMillis }.average().toLong()

        return Batch.Response(
            size = responseList.size,
            totalSearchDurationMillis = totalDuration,
            avgSearchDurationMillis = totalDuration / responseList.size.toDouble(),
            avgAccuracy = responseList.map { it.accuracy }.average(),
            list = responseList
        )
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val payload = message.payload
        val searchResponse = search(ApiSearch.Request(payload))
        session.sendMessage(TextMessage(objectMapper.writeValueAsString(searchResponse)))
    }
}