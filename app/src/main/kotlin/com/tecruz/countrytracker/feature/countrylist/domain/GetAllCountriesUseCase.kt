package com.tecruz.countrytracker.features.countrylist.domain

import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.core.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all countries.
 * Encapsulates business logic and provides a single entry point for this operation.
 */
class GetAllCountriesUseCase @Inject constructor(
    private val repository: CountryRepository
) {
    operator fun invoke(): Flow<List<Country>> {
        return repository.getAllCountries()
    }
}
