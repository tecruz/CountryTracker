package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.features.countrydetail.domain.model.CountryDetail
import com.tecruz.countrytracker.features.countrydetail.domain.repository.CountryDetailRepository

/**
 * Use case for retrieving a country by its code.
 * Encapsulates the business logic for fetching country details.
 */
class GetCountryByCodeUseCase(private val repository: CountryDetailRepository) {
    suspend operator fun invoke(countryCode: String): CountryDetail? = repository.getCountryByCode(countryCode)
}
