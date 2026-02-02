package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.features.countrydetail.domain.model.CountryDetail
import com.tecruz.countrytracker.features.countrydetail.domain.repository.CountryDetailRepository
import javax.inject.Inject

/**
 * Use case for updating country notes.
 * Encapsulates validation and business logic for updating travel notes.
 */
class UpdateCountryNotesUseCase @Inject constructor(private val repository: CountryDetailRepository) {
    suspend operator fun invoke(country: CountryDetail, notes: String) {
        // Validate notes length
        require(notes.length <= CountryDetail.MAX_NOTES_LENGTH) {
            "Notes cannot exceed ${CountryDetail.MAX_NOTES_LENGTH} characters"
        }

        val updatedCountry = country.copy(notes = notes)
        repository.updateCountry(updatedCountry)
    }
}
