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
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.window.core.layout.WindowSizeClass
import com.tecruz.countrytracker.core.designsystem.CountryTrackerTheme
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem
import com.tecruz.countrytracker.features.countrylist.presentation.StatsCard
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.tecruz.countrytracker.features.countrylist.presentation.CountryListItem as CountryListItemComposable

/**
 * T060-T064: End-to-end adaptive layout tests.
 * Tests screen size transitions and edge cases across all breakpoints.
 */
@RunWith(AndroidJUnit4::class)
class AdaptiveLayoutE2ETest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val compactWindowSizeClass = WindowSizeClass.compute(375f, 667f)
    private val mediumWindowSizeClass = WindowSizeClass.compute(700f, 900f)
    private val expandedWindowSizeClass = WindowSizeClass.compute(900f, 1200f)

    private val testCountry = CountryListItem(
        code = "BR",
        name = "Brazil",
        region = "South America",
        visited = true,
        flagEmoji = "\uD83C\uDDE7\uD83C\uDDF7",
    )

    @Test
    fun endToEnd_compactToMediumTransition() {
        var windowSizeClass by mutableStateOf(compactWindowSizeClass)

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                    Box(modifier = Modifier.width(if (windowSizeClass == compactWindowSizeClass) 375.dp else 700.dp)) {
                        StatsCard(
                            visitedCount = 5,
                            totalCount = 75,
                            percentage = 7,
                        )
                    }
                }
            }
        }

        // Compact state
        composeTestRule.onNodeWithText("5").assertIsDisplayed()
        composeTestRule.onNodeWithText("75").assertIsDisplayed()

        // Transition to medium
        windowSizeClass = mediumWindowSizeClass
        composeTestRule.waitForIdle()

        // Data preserved
        composeTestRule.onNodeWithText("5").assertIsDisplayed()
        composeTestRule.onNodeWithText("75").assertIsDisplayed()
    }

    @Test
    fun endToEnd_mediumToExpandedTransition() {
        var windowSizeClass by mutableStateOf(mediumWindowSizeClass)

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                    Box(modifier = Modifier.width(if (windowSizeClass == mediumWindowSizeClass) 700.dp else 900.dp)) {
                        CountryListItemComposable(
                            country = testCountry,
                            onClick = {},
                        )
                    }
                }
            }
        }

        // Medium state
        composeTestRule.onNodeWithText("Brazil").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Visited").assertIsDisplayed()

        // Transition to expanded
        windowSizeClass = expandedWindowSizeClass
        composeTestRule.waitForIdle()

        // Content preserved
        composeTestRule.onNodeWithText("Brazil").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Visited").assertIsDisplayed()
    }

    @Test
    fun endToEnd_expandedToCompactTransition() {
        var windowSizeClass by mutableStateOf(expandedWindowSizeClass)

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                    Box(modifier = Modifier.width(if (windowSizeClass == expandedWindowSizeClass) 900.dp else 375.dp)) {
                        CountryListItemComposable(
                            country = testCountry,
                            onClick = {},
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Brazil").assertIsDisplayed()

        // Transition to compact (e.g., multi-window)
        windowSizeClass = compactWindowSizeClass
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Brazil").assertIsDisplayed()
    }

    @Test
    fun endToEnd_fullCycleCompactMediumExpandedAndBack() {
        var windowSizeClass by mutableStateOf(compactWindowSizeClass)

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                    val width = when (windowSizeClass) {
                        compactWindowSizeClass -> 375.dp
                        mediumWindowSizeClass -> 700.dp
                        else -> 900.dp
                    }
                    Box(modifier = Modifier.width(width)) {
                        StatsCard(
                            visitedCount = 42,
                            totalCount = 75,
                            percentage = 56,
                        )
                    }
                }
            }
        }

        // Compact
        composeTestRule.onNodeWithText("42").assertIsDisplayed()

        // To medium
        windowSizeClass = mediumWindowSizeClass
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("42").assertIsDisplayed()

        // To expanded
        windowSizeClass = expandedWindowSizeClass
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("42").assertIsDisplayed()

        // Back to compact
        windowSizeClass = compactWindowSizeClass
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("42").assertIsDisplayed()
    }
}
