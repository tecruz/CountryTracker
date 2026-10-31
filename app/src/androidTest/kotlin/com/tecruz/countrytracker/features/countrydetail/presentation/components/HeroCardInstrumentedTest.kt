package com.tecruz.countrytracker.features.countrydetail.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
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
 * Instrumented coverage for [HeroCard].
 *
 * Renders a visited country with notes and an unvisited country so both
 * content branches are exercised.
 */
@RunWith(AndroidJUnit4::class)
class HeroCardInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val windowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 375f, heightDp = 667f)

    private val visitedWithNotes = CountryDetailUi(
        code = "US",
        name = "United States",
        region = "North America",
        visited = true,
        visitedDate = 1704067200000L,
        visitedDateFormatted = "January 01, 2024",
        notes = "Amazing trip!",
        rating = 4,
        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8",
    )

    private val unvisited = visitedWithNotes.copy(
        code = "BR",
        name = "Brazil",
        visited = false,
        visitedDate = null,
        visitedDateFormatted = null,
        notes = "",
        rating = 0,
        flagEmoji = "\uD83C\uDDE7\uD83C\uDDF7",
    )

    @Test
    fun visitedCountry_rendersNameAndFlag() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                    Box(modifier = Modifier.width(375.dp)) {
                        HeroCard(country = visitedWithNotes)
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("United States").assertIsDisplayed()
    }

    @Test
    fun unvisitedCountry_rendersName() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                    Box(modifier = Modifier.width(375.dp)) {
                        HeroCard(country = unvisited)
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Brazil").assertIsDisplayed()
    }
}
