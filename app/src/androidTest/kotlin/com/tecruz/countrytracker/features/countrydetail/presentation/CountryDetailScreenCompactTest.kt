package com.tecruz.countrytracker.features.countrydetail.presentation

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
import com.tecruz.countrytracker.features.countrydetail.presentation.model.CountryDetailUi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * T013: UI test for CountryDetailScreen on compact screen (375dp).
 * Verifies that all detail components display properly on a compact screen.
 */
@RunWith(AndroidJUnit4::class)
class CountryDetailScreenCompactTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val compactWindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 375f, heightDp = 667f)

    private val testCountry = CountryDetailUi(
        code = "US",
        name = "United States",
        region = "North America",
        visited = true,
        visitedDate = 1704067200000L,
        visitedDateFormatted = "January 01, 2024",
        notes = "Amazing trip to NYC!",
        rating = 4,
        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8",
    )

    @Test
    fun heroCard_displaysCorrectlyOnCompact() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    Box(modifier = Modifier.width(375.dp)) {
                        HeroCard(country = testCountry)
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("United States").assertIsDisplayed()
        composeTestRule.onNodeWithText("North America").assertIsDisplayed()
    }

    @Test
    fun ratingCard_displaysCorrectlyOnCompact() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    Box(modifier = Modifier.width(375.dp)) {
                        RatingCard(
                            rating = 4,
                            onRatingChange = {},
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Your Rating").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Rate 1 stars").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Rate 5 stars").assertIsDisplayed()
    }

    @Test
    fun notesCard_displaysCorrectlyOnCompact() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    Box(modifier = Modifier.width(375.dp)) {
                        NotesCard(
                            notes = "Amazing trip to NYC!",
                            onEditNotes = {},
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Notes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Amazing trip to NYC!").assertIsDisplayed()
    }

    @Test
    fun visitStatusCard_displaysCorrectlyOnCompact() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    Box(modifier = Modifier.width(375.dp)) {
                        VisitStatusCard(
                            country = testCountry,
                            onEditDate = {},
                            onMarkAsUnvisited = {},
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Visited", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("January 01, 2024").assertIsDisplayed()
    }
}
