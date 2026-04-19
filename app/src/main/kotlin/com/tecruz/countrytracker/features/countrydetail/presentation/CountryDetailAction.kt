package com.tecruz.countrytracker.features.countrydetail.presentation

sealed interface CountryDetailAction {
    data class OnMarkAsVisited(val date: Long, val notes: String, val rating: Int) : CountryDetailAction
    data object OnMarkAsUnvisited : CountryDetailAction
    data class OnUpdateNotes(val notes: String) : CountryDetailAction
    data class OnUpdateRating(val rating: Int) : CountryDetailAction
    data object OnClearError : CountryDetailAction
}
