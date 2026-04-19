package com.tecruz.countrytracker.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecruz.countrytracker.features.countrydetail.presentation.CountryDetailRoot
import com.tecruz.countrytracker.features.countrylist.presentation.CountryListRoot
import kotlinx.coroutines.delay

@Composable
fun CountryTrackerNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.CountryList.route,
        modifier = modifier,
    ) {
        composable(Screen.CountryList.route) {
            CountryListRoot(
                onCountryClick = { countryCode ->
                    navController.navigate(Screen.CountryDetail.createRoute(countryCode))
                },
            )
        }

        composable(
            route = Screen.CountryDetail.route,
            arguments = listOf(
                navArgument(Screen.CountryDetail.ARG_COUNTRY_CODE) { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val countryCode = backStackEntry.arguments?.getString(Screen.CountryDetail.ARG_COUNTRY_CODE) ?: ""
            var isValidCode by remember(countryCode) { mutableStateOf(validateCountryCode(countryCode)) }

            if (isValidCode) {
                CountryDetailRoot(
                    onNavigateBack = { navController.navigateUp() },
                )
            } else {
                LaunchedEffect(Unit) {
                    delay(100)
                    navController.navigateUp()
                }
            }
        }
    }
}

private fun validateCountryCode(code: String): Boolean {
    if (code.isBlank()) return false
    if (code.length !in 2..3) return false
    return code.all { it.isUpperCase() || it.isDigit() }
}
