package com.tecruz.countrytracker.features.countrylist.presentation

import com.tecruz.countrytracker.core.presentation.UiText

sealed interface CountryListEvent {
    data class NavigateToDetail(val countryCode: String) : CountryListEvent
    data class ShowSnackbar(val message: UiText) : CountryListEvent
}
