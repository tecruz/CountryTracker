package com.tecruz.countrytracker.features.countrylist.domain.model

import com.tecruz.countrytracker.core.domain.model.Country

/**
 * Domain model for country list items.
 * Contains only the fields needed for displaying countries in a list.
 */
data class CountryListItem(
    val code: String,
    val name: String,
    val region: String,
    val visited: Boolean,
    val flagEmoji: String,
)

fun Country.toCountryListItem(): CountryListItem = CountryListItem(
    code = code,
    name = name,
    region = region,
    visited = visited,
    flagEmoji = flagEmoji,
)
