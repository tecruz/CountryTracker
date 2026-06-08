package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.core.domain.repository.CountryRepository

/**
 * Use case for updating country rating.
 */
class UpdateCountryRatingUseCase(private val repository: CountryRepository) {
    suspend operator fun invoke(country: Country, rating: Int) {
        require(rating in Country.MIN_RATING..Country.MAX_RATING) {
            "Rating must be between ${Country.MIN_RATING} and ${Country.MAX_RATING}"
        }

        val updatedCountry = country.copy(rating = rating)
        repository.updateCountry(updatedCountry)
    }
}
