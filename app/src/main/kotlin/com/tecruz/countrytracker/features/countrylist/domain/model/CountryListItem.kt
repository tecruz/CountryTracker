package com.tecruz.countrytracker.features.countrylist.domain.model

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
