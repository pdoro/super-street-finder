package me.pablo.streetfinder.domain.core

data class Street(
    val type: String,
    val name: String,
    val postalCode: Int,
    val number: Int,
    val country: String,
    val nexus: String
)
