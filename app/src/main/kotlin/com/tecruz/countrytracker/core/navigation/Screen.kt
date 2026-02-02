package com.tecruz.countrytracker.core.navigation

/**
 * Type-safe navigation routes for the app.
 */
sealed class Screen(val route: String) {
    data object CountryList : Screen("country_list")

    data object CountryDetail : Screen("country_detail/{countryCode}") {
        const val ARG_COUNTRY_CODE = "countryCode"

        fun createRoute(countryCode: String): String = "country_detail/$countryCode"
    }
}
