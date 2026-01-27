package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.core.domain.repository.CountryRepository
import javax.inject.Inject

/**
 * Use case for updating country rating.
 * Encapsulates validation and business logic for rating countries.
 */
class UpdateCountryRatingUseCase @Inject constructor(
    private val repository: CountryRepository
) {
    suspend operator fun invoke(country: Country, rating: Int) {
        // Validate rating range
        require(rating in MIN_RATING..MAX_RATING) {
            "Rating must be between $MIN_RATING and $MAX_RATING"
        }
        
        val updatedCountry = country.copy(rating = rating)
        repository.updateCountry(updatedCountry)
    }
    
    companion object {
        const val MIN_RATING = 0
        const val MAX_RATING = 5
    }
}
