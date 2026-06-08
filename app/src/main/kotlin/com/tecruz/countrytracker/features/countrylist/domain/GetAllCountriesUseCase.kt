package com.tecruz.countrytracker.features.countrylist.domain

import com.tecruz.countrytracker.core.domain.repository.CountryRepository
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem
import com.tecruz.countrytracker.features.countrylist.domain.model.toCountryListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case for retrieving all countries.
 * Encapsulates business logic and provides a single entry point for this operation.
 */
class GetAllCountriesUseCase(private val repository: CountryRepository) {
    operator fun invoke(
        query: String = "",
        region: String = "All",
        showOnlyVisited: Boolean = false,
    ): Flow<List<CountryListItem>> = repository.getFilteredCountries(query, region, showOnlyVisited)
        .map { countries -> countries.map { it.toCountryListItem() } }
}
