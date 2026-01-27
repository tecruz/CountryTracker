package com.tecruz.countrytracker.core.domain.repository

import com.tecruz.countrytracker.core.domain.model.Country
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for country data operations.
 */
interface CountryRepository {

    fun getAllCountries(): Flow<List<Country>>

    fun getCountriesByRegion(region: String): Flow<List<Country>>

    suspend fun getCountryByCode(code: String): Country?

    fun getVisitedCount(): Flow<Int>

    fun getTotalCount(): Flow<Int>

    fun getAllRegions(): Flow<List<String>>

    suspend fun updateCountry(country: Country)

    suspend fun markAsVisited(country: Country, date: Long, notes: String = "", rating: Int = 0)

    suspend fun markAsUnvisited(country: Country)
}
