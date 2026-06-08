package com.tecruz.countrytracker.core.data.repository

import com.tecruz.countrytracker.core.data.database.CountryDao
import com.tecruz.countrytracker.core.data.mapper.toCountry
import com.tecruz.countrytracker.core.data.mapper.toEntity
import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.core.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of CountryRepository.
 */
class CountryRepositoryImpl(private val countryDao: CountryDao) : CountryRepository {

    override fun getVisitedCount(): Flow<Int> = countryDao.getVisitedCount()

    override fun getTotalCount(): Flow<Int> = countryDao.getTotalCount()

    override fun getAllRegions(): Flow<List<String>> = countryDao.getAllRegions()

    override fun getVisitedCountryCodes(): Flow<List<String>> = countryDao.getVisitedCountryCodes()

    override fun getFilteredCountries(query: String, region: String, showOnlyVisited: Boolean): Flow<List<Country>> =
        countryDao.getFilteredCountries(query, region, showOnlyVisited).map { entities ->
            entities.map { it.toCountry() }
        }

    override suspend fun getCountryByCode(code: String): Country? = countryDao.getCountryByCode(code)?.toCountry()

    override fun getCountryByCodeFlow(code: String): Flow<Country?> =
        countryDao.getCountryByCodeFlow(code).map { it?.toCountry() }

    override suspend fun updateCountry(country: Country) {
        countryDao.updateCountry(country.toEntity())
    }

    override suspend fun markAsVisited(country: Country, date: Long, notes: String, rating: Int) {
        val updatedCountry = country.copy(
            visited = true,
            visitedDate = date,
            notes = notes,
            rating = rating,
        )
        countryDao.updateCountry(updatedCountry.toEntity())
    }

    override suspend fun markAsUnvisited(country: Country) {
        val updatedCountry = country.copy(
            visited = false,
            visitedDate = null,
            notes = "",
            rating = 0,
        )
        countryDao.updateCountry(updatedCountry.toEntity())
    }
}
