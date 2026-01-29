package com.tecruz.countrytracker.features.countrylist.data.mapper

import com.tecruz.countrytracker.core.data.database.CountryEntity
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem

/**
 * Maps CountryEntity to CountryListItem domain model.
 */
fun CountryEntity.toCountryListItem(): CountryListItem = CountryListItem(
    code = code,
    name = name,
    region = region,
    visited = visited,
    flagEmoji = flagEmoji,
)
