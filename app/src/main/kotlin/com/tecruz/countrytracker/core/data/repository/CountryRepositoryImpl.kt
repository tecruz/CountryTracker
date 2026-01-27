package com.tecruz.countrytracker.core.data.repository

import com.tecruz.countrytracker.core.data.database.CountryDao
import com.tecruz.countrytracker.core.data.database.toDomain
import com.tecruz.countrytracker.core.data.database.toEntity
import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.core.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of CountryRepository.
 */
class CountryRepositoryImpl @Inject constructor(
    private val countryDao: CountryDao
) : CountryRepository {

    override fun getAllCountries(): Flow<List<Country>> {
        return countryDao.getAllCountries().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getCountriesByRegion(region: String): Flow<List<Country>> {
        return countryDao.getCountriesByRegion(region).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCountryByCode(code: String): Country? {
        return countryDao.getCountryByCode(code)?.toDomain()
    }

    override fun getVisitedCount(): Flow<Int> {
        return countryDao.getVisitedCount()
    }

    override fun getTotalCount(): Flow<Int> {
        return countryDao.getTotalCount()
    }

    override fun getAllRegions(): Flow<List<String>> {
        return countryDao.getAllRegions()
    }

    override suspend fun updateCountry(country: Country) {
        countryDao.updateCountry(country.toEntity())
    }

    override suspend fun markAsVisited(
        country: Country,
        date: Long,
        notes: String,
        rating: Int
    ) {
        val updatedCountry = country.copy(
            visited = true,
            visitedDate = date,
            notes = notes,
            rating = rating
        )
        countryDao.updateCountry(updatedCountry.toEntity())
    }

    override suspend fun markAsUnvisited(country: Country) {
        val updatedCountry = country.copy(
            visited = false,
            visitedDate = null,
            notes = "",
            rating = 0
        )
        countryDao.updateCountry(updatedCountry.toEntity())
    }
}
