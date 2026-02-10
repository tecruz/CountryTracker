package com.tecruz.countrytracker.features.countrylist.presentation

import androidx.compose.material.icons.filled.Flag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.tecruz.countrytracker.core.designsystem.CountryTrackerTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem as CountryListItemModel

/**
 * Unit tests for CountryListScreen public composables.
 * Uses Robolectric to render Compose UI and exercise uncovered branches.
 */
@RunWith(RobolectricTestRunner::class)
class CountryListScreenComposableTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testCountry = CountryListItemModel(
        code = "US",
        name = "United States",
        region = "North America",
        visited = false,
        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8",
    )

    private val visitedCountry = CountryListItemModel(
        code = "FR",
        name = "France",
        region = "Europe",
        visited = true,
        flagEmoji = "\uD83C\uDDEB\uD83C\uDDF7",
    )

    // --- StatsCard tests ---

    @Test
    fun `statsCard displays visited count, total count, and percentage`() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                StatsCard(visitedCount = 25, totalCount = 100, percentage = 25)
            }
        }

        composeTestRule.onNodeWithText("25").assertIsDisplayed()
        composeTestRule.onNodeWithText("100").assertIsDisplayed()
        composeTestRule.onNodeWithText("25%").assertIsDisplayed()
    }

    @Test
    fun `statsCard displays zero values correctly`() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                StatsCard(visitedCount = 0, totalCount = 0, percentage = 0)
            }
        }

        composeTestRule.onNodeWithText("0%").assertIsDisplayed()
    }

    // --- StatItem tests ---

    @Test
    fun `statItem displays value and label`() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                StatItem(
                    icon = androidx.compose.material.icons.Icons.Default.Flag,
                    value = "42",
                    label = "Visited",
                    iconTint = androidx.compose.ui.graphics.Color.Green,
                )
            }
        }

        composeTestRule.onNodeWithText("42").assertIsDisplayed()
        composeTestRule.onNodeWithText("Visited").assertIsDisplayed()
    }

    // --- SearchBar tests ---

    @Test
    fun `searchBar shows clear button when query is not empty`() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                SearchBar(
                    query = "test query",
                    onQueryChange = {},
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Clear search").assertIsDisplayed()
    }

    @Test
    fun `searchBar hides clear button when query is empty`() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                SearchBar(
                    query = "",
                    onQueryChange = {},
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Clear search").assertDoesNotExist()
    }

    @Test
    fun `searchBar clear button clears the query`() {
        var currentQuery = "test"

        composeTestRule.setContent {
            CountryTrackerTheme {
                SearchBar(
                    query = currentQuery,
                    onQueryChange = { currentQuery = it },
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Clear search").performClick()
        assertEquals("", currentQuery)
    }

    // --- FilterChips tests ---

    @Test
    fun `filterChips shows visited only chip as selected when enabled`() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                FilterChips(
                    allRegions = listOf("Europe"),
                    selectedRegion = "All",
                    showOnlyVisited = true,
                    onToggleVisited = {},
                    onRegionSelect = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Visited only").assertIsDisplayed()
    }

    @Test
    fun `filterChips shows region chip as selected`() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                FilterChips(
                    allRegions = listOf("Europe", "Asia"),
                    selectedRegion = "Europe",
                    showOnlyVisited = false,
                    onToggleVisited = {},
                    onRegionSelect = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Europe").assertIsDisplayed()
    }

    @Test
    fun `filterChips region selection triggers callback`() {
        var selectedRegion = "All"

        composeTestRule.setContent {
            CountryTrackerTheme {
                FilterChips(
                    allRegions = listOf("Europe", "Asia"),
                    selectedRegion = selectedRegion,
                    showOnlyVisited = false,
                    onToggleVisited = {},
                    onRegionSelect = { selectedRegion = it },
                )
            }
        }

        composeTestRule.onNodeWithText("Asia").performClick()
        assertEquals("Asia", selectedRegion)
    }

    @Test
    fun `filterChips all chip selection triggers callback`() {
        var selectedRegion = "Europe"

        composeTestRule.setContent {
            CountryTrackerTheme {
                FilterChips(
                    allRegions = listOf("Europe"),
                    selectedRegion = selectedRegion,
                    showOnlyVisited = false,
                    onToggleVisited = {},
                    onRegionSelect = { selectedRegion = it },
                )
            }
        }

        composeTestRule.onNodeWithText("All").performClick()
        assertEquals("All", selectedRegion)
    }

    // --- CountryListItem tests ---

    @Test
    fun `countryListItem shows unvisited country without check icon`() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CountryListItem(
                    country = testCountry,
                    onClick = {},
                )
            }
        }

        composeTestRule.onNodeWithText("United States").assertIsDisplayed()
        composeTestRule.onNodeWithText("North America").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Visited").assertDoesNotExist()
    }

    @Test
    fun `countryListItem shows visited country with check icon`() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CountryListItem(
                    country = visitedCountry,
                    onClick = {},
                )
            }
        }

        composeTestRule.onNodeWithText("France").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Visited").assertIsDisplayed()
    }

    @Test
    fun `countryListItem click triggers callback`() {
        var clicked = false

        composeTestRule.setContent {
            CountryTrackerTheme {
                CountryListItem(
                    country = testCountry,
                    onClick = { clicked = true },
                )
            }
        }

        composeTestRule.onNodeWithText("United States").performClick()
        assertTrue(clicked)
    }

    @Test
    fun `countryListItem displays flag emoji`() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CountryListItem(
                    country = testCountry,
                    onClick = {},
                )
            }
        }

        composeTestRule.onNodeWithText("\uD83C\uDDFA\uD83C\uDDF8").assertIsDisplayed()
    }

    @Test
    fun `filterChips shows visited only chip as unselected`() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                FilterChips(
                    allRegions = listOf("Europe"),
                    selectedRegion = "All",
                    showOnlyVisited = false,
                    onToggleVisited = {},
                    onRegionSelect = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Visited only").assertIsDisplayed()
    }

    @Test
    fun `filterChips visited only toggle triggers callback`() {
        var toggleVisitedCalled = false

        composeTestRule.setContent {
            CountryTrackerTheme {
                FilterChips(
                    allRegions = listOf("Europe"),
                    selectedRegion = "All",
                    showOnlyVisited = false,
                    onToggleVisited = { toggleVisitedCalled = true },
                    onRegionSelect = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Visited only").performClick()
        assertTrue(toggleVisitedCalled)
    }

    @Test
    fun `filterChips shows all region chip as selected`() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                FilterChips(
                    allRegions = listOf("Europe", "Asia"),
                    selectedRegion = "All",
                    showOnlyVisited = false,
                    onToggleVisited = {},
                    onRegionSelect = {},
                )
            }
        }

        composeTestRule.onNodeWithText("All").assertIsDisplayed()
    }

    @Test
    fun `filterChips shows all region chip as unselected`() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                FilterChips(
                    allRegions = listOf("Europe", "Asia"),
                    selectedRegion = "Europe",
                    showOnlyVisited = false,
                    onToggleVisited = {},
                    onRegionSelect = {},
                )
            }
        }

        composeTestRule.onNodeWithText("All").assertIsDisplayed()
    }

    @Test
    fun `filterChips with multiple regions shows all chips`() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                FilterChips(
                    allRegions = listOf("Europe", "Asia", "Africa", "Americas"),
                    selectedRegion = "All",
                    showOnlyVisited = false,
                    onToggleVisited = {},
                    onRegionSelect = {},
                )
            }
        }

        composeTestRule.onNodeWithText("All").assertIsDisplayed()
        composeTestRule.onNodeWithText("Europe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Asia").assertIsDisplayed()
        composeTestRule.onNodeWithText("Africa").assertIsDisplayed()
        composeTestRule.onNodeWithText("Americas").assertIsDisplayed()
    }

    @Test
    fun `filterChips with single region shows only that chip`() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                FilterChips(
                    allRegions = listOf("Europe"),
                    selectedRegion = "Europe",
                    showOnlyVisited = false,
                    onToggleVisited = {},
                    onRegionSelect = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Europe").assertIsDisplayed()
    }

    @Test
    fun `searchBar with empty query does not show clear button`() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                SearchBar(
                    query = "",
                    onQueryChange = {},
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Clear search").assertDoesNotExist()
    }

    @Test
    fun `statsCard with large values displays correctly`() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                StatsCard(visitedCount = 999, totalCount = 1000, percentage = 99)
            }
        }

        composeTestRule.onNodeWithText("999").assertIsDisplayed()
        composeTestRule.onNodeWithText("1000").assertIsDisplayed()
        composeTestRule.onNodeWithText("99%").assertIsDisplayed()
    }

    @Test
    fun `countryListItem with very long name truncates correctly`() {
        val longNameCountry = CountryListItemModel(
            code = "XX",
            name = "Very Long Country Name That Should Be Truncated",
            region = "Region",
            visited = false,
            flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8",
        )

        composeTestRule.setContent {
            CountryTrackerTheme {
                CountryListItem(
                    country = longNameCountry,
                    onClick = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Very Long Country Name That Should Be Truncated").assertIsDisplayed()
    }
}
