package me.pablo.streetfinder.infrastructure.primary.api.model

class Batch {

    data class Request<T>(
        val list: List<T>
    )

    data class Response<T>(
        val size: Int,
        val totalSearchDurationMillis: Long,
        val avgSearchDurationMillis: Double,
        val avgAccuracy: Double,
        val list: List<T>
    )
}