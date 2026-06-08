package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.core.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving a country by its code.
 * Encapsulates the business logic for fetching country details.
 */
class GetCountryByCodeUseCase(private val repository: CountryRepository) {
    operator fun invoke(countryCode: String): Flow<Country?> = repository.getCountryByCodeFlow(countryCode)
}
