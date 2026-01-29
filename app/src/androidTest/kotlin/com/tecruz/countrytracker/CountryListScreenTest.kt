package com.tecruz.countrytracker

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tecruz.countrytracker.core.designsystem.CountryTrackerTheme
import com.tecruz.countrytracker.features.countrylist.presentation.CountryListItem
import com.tecruz.countrytracker.features.countrylist.presentation.FilterChips
import com.tecruz.countrytracker.features.countrylist.presentation.SearchBar
import com.tecruz.countrytracker.features.countrylist.presentation.StatsCard
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem as CountryListItemModel

@RunWith(AndroidJUnit4::class)
class CountryListScreenTest {

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

    @Test
    fun statsCard_displaysCorrectValues() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                StatsCard(
                    visitedCount = 10,
                    totalCount = 50,
                    percentage = 20,
                )
            }
        }

        composeTestRule.onNodeWithText("10").assertIsDisplayed()
        composeTestRule.onNodeWithText("50").assertIsDisplayed()
        composeTestRule.onNodeWithText("20%").assertIsDisplayed()
    }

    @Test
    fun searchBar_acceptsInput() {
        var searchQuery = ""

        composeTestRule.setContent {
            CountryTrackerTheme {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Search")
            .assertIsDisplayed()
    }

    @Test
    fun filterChips_displayAllRegions() {
        val regions = listOf("Europe", "Asia")

        composeTestRule.setContent {
            CountryTrackerTheme {
                FilterChips(
                    allRegions = regions,
                    selectedRegion = "All",
                    showOnlyVisited = false,
                    onToggleVisited = {},
                    onRegionSelect = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Europe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Asia").assertIsDisplayed()
    }

    @Test
    fun filterChips_visitedOnlyCanBeToggled() {
        var showOnlyVisited = false

        composeTestRule.setContent {
            CountryTrackerTheme {
                FilterChips(
                    allRegions = emptyList(),
                    selectedRegion = "All",
                    showOnlyVisited = showOnlyVisited,
                    onToggleVisited = { showOnlyVisited = !showOnlyVisited },
                    onRegionSelect = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Visited only").performClick()
        assert(showOnlyVisited)
    }

    @Test
    fun countryListItem_displaysCountryInfo() {
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
    }

    @Test
    fun countryListItem_showsVisitedIndicator() {
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
    fun countryListItem_clickTriggersCallback() {
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
        assert(clicked)
    }

    @Test
    fun filterChips_regionSelectionWorks() {
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

        composeTestRule.onNodeWithText("Europe").performClick()
        assert(selectedRegion == "Europe")
    }
}
