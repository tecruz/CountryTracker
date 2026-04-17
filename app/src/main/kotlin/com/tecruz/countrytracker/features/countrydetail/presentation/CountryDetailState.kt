package com.tecruz.countrytracker.features.countrydetail.presentation

import androidx.compose.runtime.Stable
import com.tecruz.countrytracker.core.presentation.UiText
import com.tecruz.countrytracker.features.countrydetail.presentation.model.CountryDetailUi

@Stable
data class CountryDetailState(
    val country: CountryDetailUi? = null,
    val isLoading: Boolean = true,
    val error: UiText? = null,
    val isSaving: Boolean = false,
)
