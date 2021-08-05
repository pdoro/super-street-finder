package me.pablo.streetfinder.domain.core

import java.time.Duration

class SearchResponse<T> (
    val data: T,
    val classificationAccuracy: Double,
    val searchScore: Float,
    val searchDuration: Duration
)