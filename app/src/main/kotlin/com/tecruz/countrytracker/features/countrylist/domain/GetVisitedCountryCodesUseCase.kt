package com.tecruz.countrytracker.features.countrylist.domain

import com.tecruz.countrytracker.core.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case for retrieving only the codes of visited countries.
 * Useful for map visualization without loading full country data.
 */
class GetVisitedCountryCodesUseCase(private val repository: CountryRepository) {
    operator fun invoke(): Flow<Set<String>> = repository.getVisitedCountryCodes().map { it.toSet() }
}
