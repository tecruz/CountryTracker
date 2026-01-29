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
        startDestination = "country_list",
        modifier = modifier,
    ) {
        composable("country_list") {
            CountryListScreen(
                onCountryClick = { countryCode ->
                    navController.navigate("country_detail/$countryCode")
                },
                viewModel = hiltViewModel(),
            )
        }

        composable(
            route = "country_detail/{countryCode}",
            arguments = listOf(navArgument("countryCode") { type = NavType.StringType }),
        ) {
            CountryDetailScreen(
                onNavigateBack = { navController.navigateUp() },
                viewModel = hiltViewModel(),
            )
        }
    }
}
