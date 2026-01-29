package com.tecruz.countrytracker.features.countrydetail.domain.model

/**
 * Domain model for country details.
 * Contains all fields needed for displaying and editing country information.
 */
data class CountryDetail(
    val code: String,
    val name: String,
    val region: String,
    val visited: Boolean,
    val visitedDate: Long?,
    val notes: String,
    val rating: Int,
    val flagEmoji: String,
)
