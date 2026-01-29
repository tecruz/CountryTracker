package com.tecruz.countrytracker.features.countrylist.domain.repository

import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for country list data operations.
 */
interface CountryListRepository {

    fun getAllCountries(): Flow<List<CountryListItem>>

    fun getCountriesByRegion(region: String): Flow<List<CountryListItem>>

    fun getVisitedCount(): Flow<Int>

    fun getTotalCount(): Flow<Int>

    fun getAllRegions(): Flow<List<String>>
}
