package me.pablo.streetfinder.domain.port.secondary

interface PreProcessor {
    fun process(value: String): String
    fun order(): Int = Integer.MAX_VALUE
}