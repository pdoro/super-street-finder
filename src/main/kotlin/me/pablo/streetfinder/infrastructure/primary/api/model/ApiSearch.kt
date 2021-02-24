package me.pablo.streetfinder.infrastructure.primary.api.model

class ApiSearch {
    data class Request(
        val input: String
    )

    data class Response(
        val street: Street,
        val rawInput: String,
        val searchDuration: Long,
        val accuracy: Double = 0.0,
        val score: Float
    ) {
        data class Street(
            val type: String,
            val name: String,
            val nexus: String,
            val postalCode: Int,
            val number: Int,
            val country: String
        )
    }
}