package me.pablo.streetfinder.domain.core

class SearchResponse<T> (
    val data: T,
    val classificationAccuracy: Double,
    val searchScore: Float
)