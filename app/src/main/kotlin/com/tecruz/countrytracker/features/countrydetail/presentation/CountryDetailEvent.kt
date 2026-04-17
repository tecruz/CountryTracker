package com.tecruz.countrytracker.features.countrydetail.presentation

import com.tecruz.countrytracker.core.presentation.UiText

sealed interface CountryDetailEvent {
    data class ShowSnackbar(val message: UiText) : CountryDetailEvent
}
