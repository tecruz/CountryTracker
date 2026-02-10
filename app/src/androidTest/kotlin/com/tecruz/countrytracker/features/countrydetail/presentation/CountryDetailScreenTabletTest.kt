package com.tecruz.countrytracker.features.countrydetail.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
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
 * T032: UI test for CountryDetailScreen on medium screen (600dp).
 * T033: UI test for CountryDetailScreen on large screen (840dp).
 * Verifies detail components display correctly at tablet sizes.
 */
@RunWith(AndroidJUnit4::class)
class CountryDetailScreenTabletTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mediumWindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 700f, heightDp = 900f)
    private val expandedWindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 900f, heightDp = 1200f)

    private val testCountry = CountryDetailUi(
        code = "JP",
        name = "Japan",
        region = "Asia",
        visited = true,
        visitedDate = 1704067200000L,
        visitedDateFormatted = "January 01, 2024",
        notes = "Beautiful temples and amazing food!",
        rating = 5,
        flagEmoji = "\uD83C\uDDEF\uD83C\uDDF5",
    )

    @Test
    fun heroCard_displaysOnMediumScreen() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides mediumWindowSizeClass) {
                    Box(modifier = Modifier.width(700.dp)) {
                        HeroCard(country = testCountry)
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Japan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Asia").assertIsDisplayed()
    }

    @Test
    fun heroCard_displaysOnExpandedScreen() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides expandedWindowSizeClass) {
                    Box(modifier = Modifier.width(900.dp)) {
                        HeroCard(country = testCountry)
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Japan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Asia").assertIsDisplayed()
    }

    @Test
    fun notesCard_displaysOnMediumScreen() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides mediumWindowSizeClass) {
                    Box(modifier = Modifier.width(700.dp)) {
                        NotesCard(
                            notes = testCountry.notes,
                            onEditNotes = {},
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Beautiful temples and amazing food!").assertIsDisplayed()
    }

    @Test
    fun ratingCard_displaysOnExpandedScreen() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides expandedWindowSizeClass) {
                    Box(modifier = Modifier.width(900.dp)) {
                        RatingCard(
                            rating = 5,
                            onRatingChange = {},
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Your Rating").assertIsDisplayed()
    }
}
