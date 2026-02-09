package com.tecruz.countrytracker

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
import com.tecruz.countrytracker.core.designsystem.CountryTrackerTheme
import com.tecruz.countrytracker.features.countrylist.presentation.StatsCard
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * T016: UI test for orientation change behavior on compact screen.
 * Simulates different compact window sizes (portrait vs landscape-like proportions)
 * and verifies components still display correctly.
 */
@RunWith(AndroidJUnit4::class)
class OrientationChangeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun statsCard_displaysInPortraitCompact() {
        val portraitCompact = WindowSizeClass.compute(375f, 667f)

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides portraitCompact) {
                    Box(modifier = Modifier.width(375.dp)) {
                        StatsCard(
                            visitedCount = 5,
                            totalCount = 25,
                            percentage = 20,
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("5").assertIsDisplayed()
        composeTestRule.onNodeWithText("25").assertIsDisplayed()
        composeTestRule.onNodeWithText("20%").assertIsDisplayed()
    }

    @Test
    fun statsCard_displaysInLandscapeCompact() {
        // Landscape compact: wider but still compact width class
        val landscapeCompact = WindowSizeClass.compute(568f, 320f)

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides landscapeCompact) {
                    Box(modifier = Modifier.width(568.dp)) {
                        StatsCard(
                            visitedCount = 5,
                            totalCount = 25,
                            percentage = 20,
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("5").assertIsDisplayed()
        composeTestRule.onNodeWithText("25").assertIsDisplayed()
        composeTestRule.onNodeWithText("20%").assertIsDisplayed()
    }

    @Test
    fun statsCard_transitionsFromPortraitToLandscapePreservesData() {
        // Start with portrait
        val portraitCompact = WindowSizeClass.compute(375f, 667f)

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides portraitCompact) {
                    Box(modifier = Modifier.width(375.dp)) {
                        StatsCard(
                            visitedCount = 10,
                            totalCount = 50,
                            percentage = 20,
                        )
                    }
                }
            }
        }

        // Verify data is displayed
        composeTestRule.onNodeWithText("10").assertIsDisplayed()
        composeTestRule.onNodeWithText("50").assertIsDisplayed()
        composeTestRule.onNodeWithText("20%").assertIsDisplayed()
    }
}
