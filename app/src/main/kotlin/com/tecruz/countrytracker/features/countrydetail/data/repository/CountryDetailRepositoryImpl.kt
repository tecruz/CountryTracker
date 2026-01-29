package com.tecruz.countrytracker.features.countrydetail.data.repository

import com.tecruz.countrytracker.core.data.database.CountryDao
import com.tecruz.countrytracker.features.countrydetail.data.mapper.toCountryDetail
import com.tecruz.countrytracker.features.countrydetail.data.mapper.toEntity
import com.tecruz.countrytracker.features.countrydetail.domain.model.CountryDetail
import com.tecruz.countrytracker.features.countrydetail.domain.repository.CountryDetailRepository
import javax.inject.Inject

/**
 * Implementation of CountryDetailRepository.
 */
class CountryDetailRepositoryImpl @Inject constructor(private val countryDao: CountryDao) : CountryDetailRepository {

    override suspend fun getCountryByCode(code: String): CountryDetail? =
        countryDao.getCountryByCode(code)?.toCountryDetail()

    override suspend fun updateCountry(country: CountryDetail) {
        countryDao.updateCountry(country.toEntity())
    }

    override suspend fun markAsVisited(country: CountryDetail, date: Long, notes: String, rating: Int) {
        val updatedCountry = country.copy(
            visited = true,
            visitedDate = date,
            notes = notes,
            rating = rating,
        )
        countryDao.updateCountry(updatedCountry.toEntity())
    }

    override suspend fun markAsUnvisited(country: CountryDetail) {
        val updatedCountry = country.copy(
            visited = false,
            visitedDate = null,
            notes = "",
            rating = 0,
        )
        countryDao.updateCountry(updatedCountry.toEntity())
    }
}
