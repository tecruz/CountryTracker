package com.tecruz.countrytracker.core.domain.model

/**
 * Domain model for a country.
 */
data class Country(
    val code: String,
    val name: String,
    val region: String,
    val visited: Boolean = false,
    val visitedDate: Long? = null,
    val notes: String = "",
    val rating: Int = 0,
    val flagEmoji: String = ""
)
