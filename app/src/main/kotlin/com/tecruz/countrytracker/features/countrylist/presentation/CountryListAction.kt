package com.tecruz.countrytracker.features.countrylist.presentation

sealed interface CountryListAction {
    data class OnSearchQueryChange(val query: String) : CountryListAction
    data class OnRegionSelect(val region: String) : CountryListAction
    data object OnToggleShowOnlyVisited : CountryListAction
    data class OnCountryClick(val code: String) : CountryListAction
    data object OnClearError : CountryListAction
}
