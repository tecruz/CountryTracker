package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.core.domain.repository.CountryRepository

/**
 * Use case for marking a country as unvisited.
 */
class MarkCountryAsUnvisitedUseCase(private val repository: CountryRepository) {
    suspend operator fun invoke(country: Country) {
        repository.markAsUnvisited(country)
    }
}
