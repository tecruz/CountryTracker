package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.core.domain.repository.CountryRepository
import javax.inject.Inject

/**
 * Use case for updating country notes.
 * Encapsulates validation and business logic for updating travel notes.
 */
class UpdateCountryNotesUseCase @Inject constructor(
    private val repository: CountryRepository
) {
    suspend operator fun invoke(country: Country, notes: String) {
        // Validate notes length
        require(notes.length <= MAX_NOTES_LENGTH) {
            "Notes cannot exceed $MAX_NOTES_LENGTH characters"
        }
        
        val updatedCountry = country.copy(notes = notes)
        repository.updateCountry(updatedCountry)
    }
    
    companion object {
        const val MAX_NOTES_LENGTH = 500
    }
}
