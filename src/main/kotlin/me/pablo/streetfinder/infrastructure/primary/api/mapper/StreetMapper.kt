package me.pablo.streetfinder.infrastructure.primary.api.mapper

import me.pablo.streetfinder.domain.core.Street
import me.pablo.streetfinder.infrastructure.primary.api.model.ApiSearch

object StreetMapper {
    fun map(street: Street) = ApiSearch.Response.Street(
        type = street.type,
        name = street.name,
        nexus = street.nexus,
        number = street.number,
        postalCode = street.postalCode,
        country = street.country
    )
}
