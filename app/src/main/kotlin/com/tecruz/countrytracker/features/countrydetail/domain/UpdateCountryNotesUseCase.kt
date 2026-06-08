package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.core.domain.repository.CountryRepository

/**
 * Use case for updating country notes.
 */
class UpdateCountryNotesUseCase(private val repository: CountryRepository) {
    suspend operator fun invoke(country: Country, notes: String) {
        require(notes.length <= Country.MAX_NOTES_LENGTH) {
            "Notes cannot exceed ${Country.MAX_NOTES_LENGTH} characters"
        }

        val updatedCountry = country.copy(notes = notes)
        repository.updateCountry(updatedCountry)
    }
}
