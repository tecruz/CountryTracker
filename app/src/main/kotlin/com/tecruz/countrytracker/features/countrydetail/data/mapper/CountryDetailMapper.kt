package com.tecruz.countrytracker.features.countrydetail.data.mapper

import com.tecruz.countrytracker.core.data.database.CountryEntity
import com.tecruz.countrytracker.features.countrydetail.domain.model.CountryDetail

/**
 * Maps CountryEntity to CountryDetail domain model.
 */
fun CountryEntity.toCountryDetail(): CountryDetail = CountryDetail(
    code = code,
    name = name,
    region = region,
    visited = visited,
    visitedDate = visitedDate,
    notes = notes,
    rating = rating,
    flagEmoji = flagEmoji,
)

/**
 * Maps CountryDetail domain model to CountryEntity.
 */
fun CountryDetail.toEntity(): CountryEntity = CountryEntity(
    code = code,
    name = name,
    region = region,
    visited = visited,
    visitedDate = visitedDate,
    notes = notes,
    rating = rating,
    flagEmoji = flagEmoji,
)
