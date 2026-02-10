package com.tecruz.countrytracker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import com.tecruz.countrytracker.core.designsystem.CountryTrackerTheme
import com.tecruz.countrytracker.features.countrydetail.presentation.HeroCard
import com.tecruz.countrytracker.features.countrydetail.presentation.NotesCard
import com.tecruz.countrytracker.features.countrydetail.presentation.RatingCard
import com.tecruz.countrytracker.features.countrydetail.presentation.model.CountryDetailUi
import com.tecruz.countrytracker.features.countrylist.presentation.StatsCard
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * T044-T048: UI tests for foldable transitions.
 * Tests fold/unfold transitions by changing WindowSizeClass dynamically
 * and verifying state preservation and layout adaptation.
 */
@RunWith(AndroidJUnit4::class)
class FoldableTransitionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val unfoldedWindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 900f, heightDp = 1200f) // Expanded
    private val foldedWindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 400f, heightDp = 800f) // Compact

    private val testCountry = CountryDetailUi(
        code = "JP",
        name = "Japan",
        region = "Asia",
        visited = true,
        visitedDate = 1704067200000L,
        visitedDateFormatted = "January 01, 2024",
        notes = "Cherry blossoms were incredible!",
        rating = 5,
        flagEmoji = "\uD83C\uDDEF\uD83C\uDDF5",
    )

    @Test
    fun foldTransition_unfoldedToFolded_preservesHeroCardContent() {
        // T044: Unfolded -> Folded transition
        var windowSizeClass by mutableStateOf(unfoldedWindowSizeClass)

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                    Box(modifier = Modifier.width(if (windowSizeClass == unfoldedWindowSizeClass) 900.dp else 400.dp)) {
                        HeroCard(country = testCountry)
                    }
                }
            }
        }

        // Verify content on unfolded screen
        composeTestRule.onNodeWithText("Japan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Asia").assertIsDisplayed()

        // Simulate fold (switch to compact)
        windowSizeClass = foldedWindowSizeClass

        composeTestRule.waitForIdle()

        // Content should still be displayed after fold
        composeTestRule.onNodeWithText("Japan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Asia").assertIsDisplayed()
    }

    @Test
    fun foldTransition_foldedToUnfolded_preservesContent() {
        // T045: Folded -> Unfolded transition
        var windowSizeClass by mutableStateOf(foldedWindowSizeClass)

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                    Box(modifier = Modifier.width(if (windowSizeClass == foldedWindowSizeClass) 400.dp else 900.dp)) {
                        NotesCard(
                            notes = testCountry.notes,
                            onEditNotes = {},
                        )
                    }
                }
            }
        }

        // Verify on folded
        composeTestRule.onNodeWithText("Cherry blossoms were incredible!").assertIsDisplayed()

        // Simulate unfold
        windowSizeClass = unfoldedWindowSizeClass

        composeTestRule.waitForIdle()

        // Content preserved after unfold
        composeTestRule.onNodeWithText("Cherry blossoms were incredible!").assertIsDisplayed()
    }

    @Test
    fun foldTransition_statsCardPreservesDataDuringFold() {
        // T046: Scroll position / data preservation
        var windowSizeClass by mutableStateOf(unfoldedWindowSizeClass)

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                    Box(modifier = Modifier.width(if (windowSizeClass == unfoldedWindowSizeClass) 900.dp else 400.dp)) {
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

        // Fold
        windowSizeClass = foldedWindowSizeClass
        composeTestRule.waitForIdle()

        // Data preserved
        composeTestRule.onNodeWithText("15").assertIsDisplayed()
        composeTestRule.onNodeWithText("75").assertIsDisplayed()
    }

    @Test
    fun foldTransition_countryNotesPreservedDuringFold() {
        // T047: User state preservation (notes) during fold
        var windowSizeClass by mutableStateOf(unfoldedWindowSizeClass)

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                    Box(modifier = Modifier.width(if (windowSizeClass == unfoldedWindowSizeClass) 900.dp else 400.dp)) {
                        NotesCard(
                            notes = "My trip notes that should persist during fold",
                            onEditNotes = {},
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("My trip notes that should persist during fold").assertIsDisplayed()

        // Fold and unfold
        windowSizeClass = foldedWindowSizeClass
        composeTestRule.waitForIdle()
        windowSizeClass = unfoldedWindowSizeClass
        composeTestRule.waitForIdle()

        // Notes still displayed
        composeTestRule.onNodeWithText("My trip notes that should persist during fold").assertIsDisplayed()
    }

    @Test
    fun foldTransition_ratingPreservedDuringFold() {
        // T048: Rating state preservation during fold
        var windowSizeClass by mutableStateOf(unfoldedWindowSizeClass)

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                    Box(modifier = Modifier.width(if (windowSizeClass == unfoldedWindowSizeClass) 900.dp else 400.dp)) {
                        RatingCard(
                            rating = 4,
                            onRatingChange = {},
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Your Rating").assertIsDisplayed()

        // Fold
        windowSizeClass = foldedWindowSizeClass
        composeTestRule.waitForIdle()

        // Rating card still displayed
        composeTestRule.onNodeWithText("Your Rating").assertIsDisplayed()
    }
}
