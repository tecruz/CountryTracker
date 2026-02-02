package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.features.countrydetail.domain.model.CountryDetail
import com.tecruz.countrytracker.features.countrydetail.domain.repository.CountryDetailRepository
import javax.inject.Inject

/**
 * Use case for marking a country as visited.
 * Handles the business logic of updating country status with visit information.
 */
class MarkCountryAsVisitedUseCase @Inject constructor(private val repository: CountryDetailRepository) {
    suspend operator fun invoke(country: CountryDetail, date: Long, notes: String = "", rating: Int = 0) {
        require(date > 0) { "Visit date must be positive" }
        require(notes.length <= CountryDetail.MAX_NOTES_LENGTH) {
            "Notes cannot exceed ${CountryDetail.MAX_NOTES_LENGTH} characters"
        }
        require(rating in CountryDetail.MIN_RATING..CountryDetail.MAX_RATING) {
            "Rating must be between ${CountryDetail.MIN_RATING} and ${CountryDetail.MAX_RATING}"
        }

        repository.markAsVisited(country, date, notes, rating)
    }
}
