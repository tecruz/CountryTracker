package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.core.domain.repository.CountryRepository
import javax.inject.Inject

/**
 * Use case for marking a country as unvisited.
 * Handles the business logic of clearing visit information from a country.
 */
class MarkCountryAsUnvisitedUseCase @Inject constructor(
    private val repository: CountryRepository
) {
    suspend operator fun invoke(country: Country) {
        repository.markAsUnvisited(country)
    }
}
