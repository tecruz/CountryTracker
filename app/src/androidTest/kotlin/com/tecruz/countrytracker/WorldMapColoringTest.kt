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
 * Test for verifying that countries are correctly colored on the world map
 * when marked as visited.
 *
 * The WorldMapCanvas uses semantic contentDescription to expose which countries
 * are currently marked as visited, enabling verification of map coloring state.
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class WorldMapColoringTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun worldMap_whenCountryMarkedAsVisited_showsCountryCodeInSemantics() {
        // Wait for the app to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Country Tracker", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Verify the world map is displayed on the Map tab (default tab)
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithTag("world_map")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("world_map").assertIsDisplayed()

        // Mark Germany as visited
        markCountryAsVisited("Germany")

        // Switch to Map tab
        composeTestRule.onNodeWithText("Map").performClick()

        // Wait for map to load
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithTag("world_map")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Verify Germany (DE) is now in the visited countries on the map
        composeTestRule.onNodeWithTag("world_map")
            .assertContentDescriptionContains("DE", substring = true)
    }

    @Test
    fun worldMap_multipleCountriesVisited_showsAllCountryCodesInSemantics() {
        // Wait for the app to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Country Tracker", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Mark France as visited
        markCountryAsVisited("France")

        // Mark Italy as visited
        markCountryAsVisited("Italy")

        // Switch to Map tab
        composeTestRule.onNodeWithText("Map").performClick()

        // Wait for map to load
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithTag("world_map")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Verify both France (FR) and Italy (IT) are in the visited countries
        // Note: The map now correctly shows ALL visited countries regardless of search filter
        composeTestRule.onNodeWithTag("world_map")
            .assertContentDescriptionContains("FR", substring = true)
        composeTestRule.onNodeWithTag("world_map")
            .assertContentDescriptionContains("IT", substring = true)
    }

    /**
     * Helper function to mark a country as visited.
     */
    private fun markCountryAsVisited(countryName: String) {
        // Switch to Countries tab
        composeTestRule.onNodeWithText("Countries").performClick()

        // Wait for Countries tab to load
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodes(hasSetTextAction())
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Clear existing search text and enter new search
        composeTestRule.onNode(hasSetTextAction())
            .performTextReplacement(countryName)

        composeTestRule.waitForIdle()

        // Wait for search results
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("country_item_$countryName")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Click on the country
        composeTestRule.onNodeWithTag("country_item_$countryName").performClick()

        // Wait for detail screen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithContentDescription("Navigate back")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Check if already visited
        val alreadyVisited = composeTestRule
            .onAllNodesWithText("Mark as not visited", substring = true)
            .fetchSemanticsNodes().isNotEmpty()

        if (!alreadyVisited) {
            // Mark as visited
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

        // Navigate back to list
        composeTestRule.onNodeWithContentDescription("Navigate back").performClick()

        // Wait for list screen
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Country Tracker")
                .fetchSemanticsNodes().isNotEmpty()
        }
    }
}
