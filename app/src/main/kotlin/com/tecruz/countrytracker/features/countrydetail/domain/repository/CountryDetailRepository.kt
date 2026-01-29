package com.tecruz.countrytracker.features.countrydetail.domain.repository

import com.tecruz.countrytracker.features.countrydetail.domain.model.CountryDetail

/**
 * Repository interface for country detail data operations.
 */
interface CountryDetailRepository {

    suspend fun getCountryByCode(code: String): CountryDetail?

    suspend fun updateCountry(country: CountryDetail)

    suspend fun markAsVisited(country: CountryDetail, date: Long, notes: String = "", rating: Int = 0)

    suspend fun markAsUnvisited(country: CountryDetail)
}
