package com.tecruz.countrytracker.features.countrylist.data.repository

import com.tecruz.countrytracker.core.data.database.CountryDao
import com.tecruz.countrytracker.features.countrylist.data.mapper.toCountryListItem
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem
import com.tecruz.countrytracker.features.countrylist.domain.repository.CountryListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of CountryListRepository.
 */
class CountryListRepositoryImpl @Inject constructor(private val countryDao: CountryDao) : CountryListRepository {

    override fun getAllCountries(): Flow<List<CountryListItem>> = countryDao.getAllCountries().map { entities ->
        entities.map { it.toCountryListItem() }
    }

    override fun getCountriesByRegion(region: String): Flow<List<CountryListItem>> =
        countryDao.getCountriesByRegion(region).map { entities ->
            entities.map { it.toCountryListItem() }
        }

    override fun getVisitedCount(): Flow<Int> = countryDao.getVisitedCount()

    override fun getTotalCount(): Flow<Int> = countryDao.getTotalCount()

    override fun getAllRegions(): Flow<List<String>> = countryDao.getAllRegions()
}
