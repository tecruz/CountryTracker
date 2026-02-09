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
import com.tecruz.countrytracker.LocalWindowSizeClass
import com.tecruz.countrytracker.core.designsystem.CountryTrackerTheme
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * T012: UI test for CountryListScreen components on compact screen (375dp).
 * Verifies that all components display properly without clipping on a compact screen.
 */
@RunWith(AndroidJUnit4::class)
class CountryListScreenCompactTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val compactWindowSizeClass = WindowSizeClass.compute(375f, 667f)

    private val testCountries = listOf(
        CountryListItem(
            code = "US",
            name = "United States of America",
            region = "North America",
            visited = false,
            flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8",
        ),
        CountryListItem(
            code = "FR",
            name = "France",
            region = "Europe",
            visited = true,
            flagEmoji = "\uD83C\uDDEB\uD83C\uDDF7",
        ),
    )

    @Test
    fun statsCard_displaysCorrectlyOnCompact() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    Box(modifier = Modifier.width(375.dp)) {
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
    fun searchBar_displaysOnCompact() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    Box(modifier = Modifier.width(375.dp)) {
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

    @Test
    fun countryListItem_displaysFullNameOnCompact() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    Box(modifier = Modifier.width(375.dp)) {
                        CountryListItem(
                            country = testCountries[0],
                            onClick = {},
                        )
                    }
                }
            }
        }

        // Country name should be fully visible without truncation
        composeTestRule.onNodeWithText("United States of America").assertIsDisplayed()
        composeTestRule.onNodeWithText("North America").assertIsDisplayed()
    }

    @Test
    fun countryListItem_showsVisitedIndicatorOnCompact() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    Box(modifier = Modifier.width(375.dp)) {
                        CountryListItem(
                            country = testCountries[1],
                            onClick = {},
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("France").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Visited").assertIsDisplayed()
    }

    @Test
    fun filterChips_displayOnCompact() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    Box(modifier = Modifier.width(375.dp)) {
                        FilterChips(
                            allRegions = listOf("Europe", "Asia", "North America"),
                            selectedRegion = "All",
                            showOnlyVisited = false,
                            onToggleVisited = {},
                            onRegionSelect = {},
                        )
                    }
                }
            }
        }

        // At minimum, the visited only chip and All chip should be visible
        composeTestRule.onNodeWithText("Visited only").assertIsDisplayed()
        composeTestRule.onNodeWithText("All").assertIsDisplayed()
    }
}
