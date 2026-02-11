package com.tecruz.countrytracker.features.countrydetail.presentation

import com.tecruz.countrytracker.features.countrydetail.presentation.model.CountryDetailUi

data class CountryDetailUiState(
    val country: CountryDetailUi? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSaving: Boolean = false,
)
