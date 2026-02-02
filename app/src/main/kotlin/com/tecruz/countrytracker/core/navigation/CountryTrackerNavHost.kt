package com.tecruz.countrytracker.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecruz.countrytracker.features.countrydetail.presentation.CountryDetailScreen
import com.tecruz.countrytracker.features.countrylist.presentation.CountryListScreen

@Composable
fun CountryTrackerNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.CountryList.route,
        modifier = modifier,
    ) {
        composable(Screen.CountryList.route) {
            CountryListScreen(
                onCountryClick = { countryCode ->
                    navController.navigate(Screen.CountryDetail.createRoute(countryCode))
                },
                viewModel = hiltViewModel(),
            )
        }

        composable(
            route = Screen.CountryDetail.route,
            arguments = listOf(
                navArgument(Screen.CountryDetail.ARG_COUNTRY_CODE) { type = NavType.StringType },
            ),
        ) {
            CountryDetailScreen(
                onNavigateBack = { navController.navigateUp() },
                viewModel = hiltViewModel(),
            )
        }
    }
}
