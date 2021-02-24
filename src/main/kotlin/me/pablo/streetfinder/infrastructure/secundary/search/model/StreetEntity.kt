package me.pablo.streetfinder.infrastructure.secundary.search.model

import me.pablo.streetfinder.domain.core.StreetField
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.*

@Document(indexName = "streets")
data class StreetEntity(
    @field:Id
    val id: String? = null,

    @field:Field(name = StreetField.TYPE, type = FieldType.Text, analyzer = "ngram_analyzer", searchAnalyzer = "ngram_analyzer")
    val type: String,

    @field:Field(name = StreetField.NAME, type = FieldType.Text, analyzer = "ngram_analyzer", searchAnalyzer = "ngram_analyzer")
    val name: String,

    @field:Field(name = StreetField.NEXUS, type = FieldType.Text)
    val nexus: String,

    @field:Field(name = StreetField.POSTAL_CODE, type = FieldType.Integer)
    val postalCode: Int,

    @field:Field(name = StreetField.NUMBER, type = FieldType.Integer)
    val number: Int,

    @field:Field(name = StreetField.COUNTRY, type = FieldType.Keyword)
    val country: String
)