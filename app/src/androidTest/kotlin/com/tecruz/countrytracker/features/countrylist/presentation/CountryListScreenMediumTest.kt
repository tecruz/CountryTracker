package com.tecruz.countrytracker.features.countrylist.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import com.tecruz.countrytracker.LocalWindowSizeClass
import com.tecruz.countrytracker.core.designsystem.CountryTrackerTheme
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * T030: UI test for CountryListScreen on medium screen (600dp).
 * T031: UI test for CountryListScreen on large screen (840dp).
 * Verifies components display correctly at tablet sizes.
 */
@RunWith(AndroidJUnit4::class)
class CountryListScreenMediumTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mediumWindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 700f, heightDp = 900f)
    private val expandedWindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 900f, heightDp = 1200f)

    @Test
    fun statsCard_displaysOnMediumScreen() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides mediumWindowSizeClass) {
                    Box(modifier = Modifier.width(700.dp)) {
                        StatsCard(
                            visitedCount = 10,
                            totalCount = 50,
                            percentage = 20,
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("10").assertIsDisplayed()
        composeTestRule.onNodeWithText("50").assertIsDisplayed()
        composeTestRule.onNodeWithText("20%").assertIsDisplayed()
    }

    @Test
    fun statsCard_displaysOnExpandedScreen() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides expandedWindowSizeClass) {
                    Box(modifier = Modifier.width(900.dp)) {
                        StatsCard(
                            visitedCount = 15,
                            totalCount = 75,
                            percentage = 20,
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("15").assertIsDisplayed()
        composeTestRule.onNodeWithText("75").assertIsDisplayed()
        composeTestRule.onNodeWithText("20%").assertIsDisplayed()
    }

    @Test
    fun countryListItem_displaysOnMediumScreen() {
        val testCountry = CountryListItem(
            code = "US",
            name = "United States of America",
            region = "North America",
            visited = true,
            flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8",
        )

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides mediumWindowSizeClass) {
                    Box(modifier = Modifier.width(700.dp)) {
                        CountryListItem(
                            country = testCountry,
                            onClick = {},
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("United States of America").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Visited").assertIsDisplayed()
    }

    @Test
    fun searchBar_displaysOnExpandedScreen() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides expandedWindowSizeClass) {
                    Box(modifier = Modifier.width(900.dp)) {
                        SearchBar(
                            query = "",
                            onQueryChange = {},
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Search").assertIsDisplayed()
    }
}
