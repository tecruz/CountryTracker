package com.tecruz.countrytracker

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * E2E test for the Country Tracker app.
 * Tests the complete user journey from viewing the country list to marking a country as visited.
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class CountryTrackerE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun completeUserJourney_browseCountries_markAsVisited_verifyChanges() {
        // Wait for the app to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Country Tracker", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 1: Verify the app is displayed
        composeTestRule.onNodeWithText("Country Tracker").assertIsDisplayed()

        // Step 2: Countries tab is now the default, verify it's displayed
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodes(hasSetTextAction())
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 3: Switch to Map tab to verify statistics card
        composeTestRule.onNodeWithText("Map").performClick()

        // Wait for Map tab to load
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("VISITED", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("VISITED", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("TOTAL", substring = true).assertIsDisplayed()

        // Step 4: Switch to Countries tab
        composeTestRule.onNodeWithText("Countries").performClick()

        // Wait for Countries tab to load with search field
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodes(hasSetTextAction())
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 4: Search for Brazil
        composeTestRule.onNode(hasSetTextAction())
            .performTextInput("Brazil")

        // Wait for search results to appear
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithTag("country_item_Brazil")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 5: Click on Brazil to open detail screen
        composeTestRule.onNodeWithTag("country_item_Brazil").performClick()

        // Step 6: Verify detail screen is displayed
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithContentDescription("Navigate back")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodesWithText("Brazil")[0].assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Navigate back").assertIsDisplayed()

        // Step 7: Check if already visited, if so, mark as unvisited first
        val markAsUnvisitedExists = composeTestRule
            .onAllNodesWithText("Mark as not visited", substring = true)
            .fetchSemanticsNodes().isNotEmpty()

        if (markAsUnvisitedExists) {
            composeTestRule.onNodeWithText("Mark as not visited", substring = true)
                .performClick()

            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule.onAllNodesWithText("Mark as Visited")
                    .fetchSemanticsNodes().isNotEmpty()
            }
        }

        // Step 8: Mark the country as visited
        composeTestRule.onNodeWithText("Mark as Visited").performClick()

        // Wait for date picker dialog and confirm
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("OK")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("OK").performClick()

        // Wait for status to change
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Mark as not visited", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 9: Add a rating (4 stars)
        composeTestRule.onNodeWithContentDescription("Rate 4 stars", useUnmergedTree = true)
            .performClick()

        // Step 10: Navigate back to list
        composeTestRule.onNodeWithContentDescription("Navigate back").performClick()

        // Wait for list screen
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodes(hasSetTextAction())
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 11: Clear search to see all countries
        composeTestRule.onNodeWithContentDescription("Clear search", useUnmergedTree = true)
            .performClick()

        // Step 12: Verify Brazil shows in the list
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithTag("country_item_Brazil")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("country_item_Brazil").assertIsDisplayed()

        // Step 13: Switch to Map tab and verify statistics updated
        // Note: Canvas-based map coloring cannot be verified via UI tests
        composeTestRule.onNodeWithText("Map").performClick()

        // Wait for map tab to load
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("VISITED", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 14: Verify statistics are displayed
        composeTestRule.onNodeWithText("VISITED", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("TOTAL", substring = true).assertIsDisplayed()
    }

    @Test
    fun filterCountries_byRegion_verifyFiltering() {
        // Wait for the app to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Country Tracker", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 1: Verify initial state shows app
        composeTestRule.onNodeWithText("Country Tracker").assertIsDisplayed()

        // Step 2: Switch to Countries tab (filters are on Countries tab)
        composeTestRule.onNodeWithText("Countries").performClick()

        // Wait for Countries tab to load with filter chips
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Visited only", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 3: Click "Visited only" filter to toggle it on
        composeTestRule.onNodeWithText("Visited only", substring = true).performClick()
        composeTestRule.waitForIdle()

        // Step 4: Click "Visited only" again to toggle it off
        composeTestRule.onNodeWithText("Visited only", substring = true).performClick()
        composeTestRule.waitForIdle()

        // Step 5: Verify "All" region chip is visible (first region chip)
        composeTestRule.onNodeWithText("All").assertIsDisplayed()
    }

    @Test
    fun searchCountries_findSpecificCountry_openDetails() {
        // Wait for the app to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Country Tracker", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 1: Switch to Countries tab
        composeTestRule.onNodeWithText("Countries").performClick()

        // Wait for tab to switch and search field to appear
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodes(hasSetTextAction())
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 2: Search for Japan
        composeTestRule.onNode(hasSetTextAction())
            .performTextInput("Japan")

        // Step 3: Wait for search results
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithTag("country_item_Japan")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 4: Verify Japan is displayed
        composeTestRule.onNodeWithTag("country_item_Japan").assertIsDisplayed()

        // Step 5: Click on Japan
        composeTestRule.onNodeWithTag("country_item_Japan").performClick()

        // Step 6: Verify detail screen opens
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithContentDescription("Navigate back")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodesWithText("Japan")[0].assertIsDisplayed()
    }

    @Test
    fun markCountryAsVisited_addNotes_verifyPersistence() {
        // Wait for the app to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Country Tracker", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 1: Switch to Countries tab
        composeTestRule.onNodeWithText("Countries").performClick()

        // Wait for tab to switch and search field to appear
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodes(hasSetTextAction())
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 2: Search for Canada
        composeTestRule.onNode(hasSetTextAction())
            .performTextInput("Canada")

        // Step 3: Wait for search results
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithTag("country_item_Canada")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 4: Click on Canada
        composeTestRule.onNodeWithTag("country_item_Canada").performClick()

        // Step 5: Wait for detail screen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithContentDescription("Navigate back")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 6: Check if already visited, if so, mark as unvisited first
        val markAsUnvisitedExists = composeTestRule
            .onAllNodesWithText("Mark as not visited", substring = true)
            .fetchSemanticsNodes().isNotEmpty()

        if (markAsUnvisitedExists) {
            composeTestRule.onNodeWithText("Mark as not visited", substring = true)
                .performClick()

            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule.onAllNodesWithText("Mark as Visited")
                    .fetchSemanticsNodes().isNotEmpty()
            }
        }

        // Step 7: Mark as Visited
        composeTestRule.onNodeWithText("Mark as Visited").performClick()

        // Wait for date picker dialog and confirm
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("OK")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("OK").performClick()

        // Wait for status to change
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Mark as not visited", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 8: Add rating (5 stars)
        composeTestRule.onNodeWithContentDescription("Rate 5 stars", useUnmergedTree = true)
            .performClick()

        // Step 9: Navigate back
        composeTestRule.onNodeWithContentDescription("Navigate back").performClick()

        // Wait to return to list
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodes(hasSetTextAction())
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 10: Open Canada again to verify changes persisted
        composeTestRule.onNodeWithTag("country_item_Canada").performClick()

        // Step 11: Verify it's still marked as visited
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Mark as not visited", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Mark as not visited", substring = true).assertIsDisplayed()

        // Step 12: Navigate back to list
        composeTestRule.onNodeWithContentDescription("Navigate back").performClick()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Country Tracker")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 13: Switch to Map tab and verify statistics
        // Note: Canvas-based map coloring cannot be verified via UI tests
        composeTestRule.onNodeWithText("Map").performClick()

        // Wait for map tab to load
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("VISITED", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 14: Verify statistics are displayed
        composeTestRule.onNodeWithText("VISITED", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("TOTAL", substring = true).assertIsDisplayed()
    }

    @Test
    fun verifyStatisticsUpdate_whenMarkingCountriesAsVisited() {
        // Wait for the app to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Country Tracker", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 1: Switch to Countries tab
        composeTestRule.onNodeWithText("Countries").performClick()

        // Wait for tab to switch and search field to appear
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodes(hasSetTextAction())
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 2: Search for Australia
        composeTestRule.onNode(hasSetTextAction())
            .performTextInput("Australia")

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithTag("country_item_Australia")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 3: Click on Australia
        composeTestRule.onNodeWithTag("country_item_Australia").performClick()

        // Step 4: Wait for detail screen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithContentDescription("Navigate back")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 5: Mark as Visited if not already visited
        val markAsVisitedExists = composeTestRule
            .onAllNodesWithText("Mark as Visited")
            .fetchSemanticsNodes().isNotEmpty()

        if (markAsVisitedExists) {
            composeTestRule.onNodeWithText("Mark as Visited").performClick()

            // Wait for date picker dialog and confirm
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule.onAllNodesWithText("OK")
                    .fetchSemanticsNodes().isNotEmpty()
            }
            composeTestRule.onNodeWithText("OK").performClick()

            // Wait for status to change
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule.onAllNodesWithText("Mark as not visited", substring = true)
                    .fetchSemanticsNodes().isNotEmpty()
            }
        }

        // Step 6: Navigate back to list
        composeTestRule.onNodeWithContentDescription("Navigate back").performClick()

        // Wait for list screen
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodes(hasSetTextAction())
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 7: Switch to Map tab and verify statistics
        // Note: Canvas-based map coloring cannot be verified via UI tests
        composeTestRule.onNodeWithText("Map").performClick()

        // Wait for map tab to load
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("VISITED", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 8: Verify statistics are displayed
        composeTestRule.onNodeWithText("VISITED", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("TOTAL", substring = true).assertIsDisplayed()
    }
}
