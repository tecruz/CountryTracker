package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.core.domain.repository.CountryRepository
import javax.inject.Inject

/**
 * Use case for retrieving a country by its code.
 * Encapsulates the business logic for fetching country details.
 */
class GetCountryByCodeUseCase @Inject constructor(
    private val repository: CountryRepository
) {
    suspend operator fun invoke(countryCode: String): Country? {
        return repository.getCountryByCode(countryCode)
    }
}
