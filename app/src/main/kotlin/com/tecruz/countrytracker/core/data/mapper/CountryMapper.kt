package com.tecruz.countrytracker.core.data.mapper

import com.tecruz.countrytracker.core.data.database.CountryEntity
import com.tecruz.countrytracker.core.domain.model.Country

/**
 * Mappers for Country data and domain models.
 */

fun CountryEntity.toCountry(): Country = Country(
    code = code,
    name = name,
    region = region,
    visited = visited,
    visitedDate = visitedDate,
    notes = notes,
    rating = rating,
    flagEmoji = flagEmoji,
)

fun Country.toEntity(): CountryEntity = CountryEntity(
    code = code,
    name = name,
    region = region,
    visited = visited,
    visitedDate = visitedDate,
    notes = notes,
    rating = rating,
    flagEmoji = flagEmoji,
)
