package com.tecruz.countrytracker

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.features.countrydetail.presentation.HeroCard
import com.tecruz.countrytracker.features.countrydetail.presentation.RatingCard
import com.tecruz.countrytracker.features.countrydetail.presentation.NotesCard
import com.tecruz.countrytracker.features.countrydetail.presentation.VisitStatusCard
import com.tecruz.countrytracker.core.designsystem.CountryTrackerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CountryDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testCountry = Country(
        code = "US",
        name = "United States",
        region = "North America",
        visited = true,
        visitedDate = 1704067200000L,
        notes = "Amazing trip to NYC!",
        rating = 4,
        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8"
    )

    @Test
    fun heroCard_displaysCountryInfo() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                HeroCard(country = testCountry)
            }
        }

        composeTestRule.onNodeWithText("United States").assertIsDisplayed()
        composeTestRule.onNodeWithText("North America").assertIsDisplayed()
    }

    @Test
    fun ratingCard_displaysCurrentRating() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                RatingCard(
                    rating = 4,
                    onRatingChange = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Your Rating").assertIsDisplayed()
    }

    @Test
    fun ratingCard_allowsRatingChange() {
        var currentRating = 0

        composeTestRule.setContent {
            CountryTrackerTheme {
                RatingCard(
                    rating = currentRating,
                    onRatingChange = { currentRating = it }
                )
            }
        }

        // Click on the 3rd star
        composeTestRule.onNodeWithContentDescription("Rate 3 stars").performClick()
        assert(currentRating == 3)
    }

    @Test
    fun notesCard_displaysNotes() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                NotesCard(
                    notes = "Amazing trip to NYC!",
                    onEditNotes = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Notes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Amazing trip to NYC!").assertIsDisplayed()
    }

    @Test
    fun notesCard_showsPlaceholderWhenEmpty() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                NotesCard(
                    notes = "",
                    onEditNotes = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Notes").assertIsDisplayed()
        composeTestRule.onNodeWithText("No notes yet. Tap edit to add your memories!").assertIsDisplayed()
    }

    @Test
    fun notesCard_editButtonTriggersCallback() {
        var editClicked = false

        composeTestRule.setContent {
            CountryTrackerTheme {
                NotesCard(
                    notes = "Some notes",
                    onEditNotes = { editClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Edit notes").performClick()
        assert(editClicked)
    }

    @Test
    fun visitStatusCard_showsVisitedStatus() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                VisitStatusCard(
                    country = testCountry,
                    onEditDate = {},
                    onMarkAsUnvisited = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Visited", substring = true).assertIsDisplayed()
    }

    @Test
    fun visitStatusCard_markAsUnvisitedWorks() {
        var unvisitedClicked = false

        composeTestRule.setContent {
            CountryTrackerTheme {
                VisitStatusCard(
                    country = testCountry,
                    onEditDate = {},
                    onMarkAsUnvisited = { unvisitedClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Mark as not visited").performClick()
        assert(unvisitedClicked)
    }

    @Test
    fun visitStatusCard_editDateButtonWorks() {
        var editDateClicked = false

        composeTestRule.setContent {
            CountryTrackerTheme {
                VisitStatusCard(
                    country = testCountry,
                    onEditDate = { editDateClicked = true },
                    onMarkAsUnvisited = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Edit visit date").performClick()
        assert(editDateClicked)
    }

    @Test
    fun ratingCard_displaysFiveStars() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                RatingCard(
                    rating = 0,
                    onRatingChange = {}
                )
            }
        }

        // Should have 5 star buttons
        composeTestRule.onNodeWithContentDescription("Rate 1 stars").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Rate 2 stars").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Rate 3 stars").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Rate 4 stars").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Rate 5 stars").assertIsDisplayed()
    }
}
