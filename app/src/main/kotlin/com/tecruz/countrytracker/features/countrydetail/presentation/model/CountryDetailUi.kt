package com.tecruz.countrytracker.features.countrydetail.presentation.model

import com.tecruz.countrytracker.features.countrydetail.domain.model.CountryDetail
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Presentation model for country details.
 * Contains UI-ready data with formatted fields.
 */
data class CountryDetailUi(
    val code: String,
    val name: String,
    val region: String,
    val visited: Boolean,
    val visitedDate: Long?,
    val visitedDateFormatted: String?,
    val notes: String,
    val rating: Int,
    val flagEmoji: String,
)

/**
 * Maps domain model to presentation model.
 */
fun CountryDetail.toUi(): CountryDetailUi {
    val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
    return CountryDetailUi(
        code = code,
        name = name,
        region = region,
        visited = visited,
        visitedDate = visitedDate,
        visitedDateFormatted = visitedDate?.let { dateFormat.format(Date(it)) },
        notes = notes,
        rating = rating,
        flagEmoji = flagEmoji,
    )
}
