package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.features.countrydetail.domain.model.CountryDetail
import com.tecruz.countrytracker.features.countrydetail.domain.repository.CountryDetailRepository
import javax.inject.Inject

/**
 * Use case for updating country rating.
 * Encapsulates validation and business logic for rating countries.
 */
class UpdateCountryRatingUseCase @Inject constructor(private val repository: CountryDetailRepository) {
    suspend operator fun invoke(country: CountryDetail, rating: Int) {
        // Validate rating range
        require(rating in CountryDetail.MIN_RATING..CountryDetail.MAX_RATING) {
            "Rating must be between ${CountryDetail.MIN_RATING} and ${CountryDetail.MAX_RATING}"
        }

        val updatedCountry = country.copy(rating = rating)
        repository.updateCountry(updatedCountry)
    }
}
