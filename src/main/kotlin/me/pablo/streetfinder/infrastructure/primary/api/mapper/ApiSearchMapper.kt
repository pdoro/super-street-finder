package me.pablo.streetfinder.infrastructure.primary.api.mapper

import me.pablo.streetfinder.domain.core.SearchResponse
import me.pablo.streetfinder.domain.core.Street
import me.pablo.streetfinder.infrastructure.primary.api.model.ApiSearch

object ApiSearchMapper {

    fun toResponse(
        request: ApiSearch.Request,
        result: SearchResponse<Street>
    ): ApiSearch.Response {
        return ApiSearch.Response(
            rawInput = request.input,
            searchDurationMillis = result.searchDuration.toMillis(),
            street = StreetMapper.map(result.data),
            accuracy = result.classificationAccuracy,
            score = result.searchScore
        )
    }
}