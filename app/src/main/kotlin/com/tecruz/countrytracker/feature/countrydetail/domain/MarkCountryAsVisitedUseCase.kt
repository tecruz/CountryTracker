package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.core.domain.repository.CountryRepository
import javax.inject.Inject

/**
 * Use case for marking a country as visited.
 * Handles the business logic of updating country status with visit information.
 */
class MarkCountryAsVisitedUseCase @Inject constructor(
    private val repository: CountryRepository
) {
    suspend operator fun invoke(
        country: Country,
        date: Long,
        notes: String = "",
        rating: Int = 0
    ) {
        repository.markAsVisited(country, date, notes, rating)
    }
}
