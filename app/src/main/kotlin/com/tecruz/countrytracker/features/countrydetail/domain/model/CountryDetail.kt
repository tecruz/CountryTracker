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
) {
    companion object {
        const val MAX_NOTES_LENGTH = 500
        const val MIN_RATING = 0
        const val MAX_RATING = 5
    }
}
