package com.tecruz.countrytracker.features.countrylist.domain

import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem
import com.tecruz.countrytracker.features.countrylist.domain.repository.CountryListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all countries.
 * Encapsulates business logic and provides a single entry point for this operation.
 */
class GetAllCountriesUseCase @Inject constructor(private val repository: CountryListRepository) {
    operator fun invoke(): Flow<List<CountryListItem>> = repository.getAllCountries()
}
