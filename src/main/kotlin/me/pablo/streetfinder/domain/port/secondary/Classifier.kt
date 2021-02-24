package me.pablo.streetfinder.domain.port.secondary

import me.pablo.streetfinder.domain.core.ClassifiedInput

interface Classifier {
    fun classify(input: String): ClassifiedInput
}