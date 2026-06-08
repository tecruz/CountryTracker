package com.tecruz.countrytracker.features.countrylist.domain

import com.tecruz.countrytracker.core.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving all distinct regions.
 */
class GetAllRegionsUseCase(private val repository: CountryRepository) {
    operator fun invoke(): Flow<List<String>> = repository.getAllRegions()
}
