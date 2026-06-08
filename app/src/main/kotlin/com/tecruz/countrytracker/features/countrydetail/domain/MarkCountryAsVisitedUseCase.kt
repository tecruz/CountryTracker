package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.core.domain.repository.CountryRepository

/**
 * Use case for marking a country as visited.
 */
class MarkCountryAsVisitedUseCase(private val repository: CountryRepository) {
    suspend operator fun invoke(country: Country, date: Long, notes: String = "", rating: Int = 0) {
        require(date > 0) { "Visited date must be positive" }
        require(notes.length <= Country.MAX_NOTES_LENGTH) {
            "Notes cannot exceed ${Country.MAX_NOTES_LENGTH} characters"
        }
        require(rating in Country.MIN_RATING..Country.MAX_RATING) {
            "Rating must be between ${Country.MIN_RATING} and ${Country.MAX_RATING}"
        }

        repository.markAsVisited(country, date, notes, rating)
    }
}
