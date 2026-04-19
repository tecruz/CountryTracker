package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.features.countrydetail.domain.model.CountryDetail
import com.tecruz.countrytracker.features.countrydetail.domain.repository.CountryDetailRepository

/**
 * Use case for marking a country as unvisited.
 * Handles the business logic of clearing visit information from a country.
 */
class MarkCountryAsUnvisitedUseCase(private val repository: CountryDetailRepository) {
    suspend operator fun invoke(country: CountryDetail) {
        repository.markAsUnvisited(country)
    }
}
