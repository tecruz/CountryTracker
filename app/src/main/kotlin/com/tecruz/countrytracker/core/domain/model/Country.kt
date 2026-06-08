package com.tecruz.countrytracker.core.domain.model

/**
 * Domain model for country.
 * Contains all fields needed for displaying and editing country information.
 */
data class Country(
    val code: String,
    val name: String,
    val region: String,
    val visited: Boolean,
    val visitedDate: Long? = null,
    val notes: String = "",
    val rating: Int = 0,
    val flagEmoji: String,
) {
    companion object {
        const val MAX_NOTES_LENGTH = 500
        const val MIN_RATING = 0
        const val MAX_RATING = 5
    }
}
