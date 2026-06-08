package com.tecruz.countrytracker.core.domain.repository

import com.tecruz.countrytracker.core.domain.model.Country
import kotlinx.coroutines.flow.Flow

/**
 * Unified repository interface for country data operations.
 */
interface CountryRepository {

    fun getVisitedCount(): Flow<Int>

    fun getTotalCount(): Flow<Int>

    fun getAllRegions(): Flow<List<String>>

    fun getVisitedCountryCodes(): Flow<List<String>>

    fun getFilteredCountries(query: String, region: String, showOnlyVisited: Boolean): Flow<List<Country>>

    suspend fun getCountryByCode(code: String): Country?

    fun getCountryByCodeFlow(code: String): Flow<Country?>

    suspend fun updateCountry(country: Country)

    suspend fun markAsVisited(country: Country, date: Long, notes: String = "", rating: Int = 0)

    suspend fun markAsUnvisited(country: Country)
}
