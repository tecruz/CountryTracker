package com.tecruz.countrytracker.core.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes for the app.
 */
sealed interface Screen {
    @Serializable
    data object CountryList : Screen

    @Serializable
    data class CountryDetail(val countryCode: String) : Screen
}
