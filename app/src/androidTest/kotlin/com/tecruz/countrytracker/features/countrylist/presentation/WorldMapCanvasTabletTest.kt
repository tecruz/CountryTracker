package com.tecruz.countrytracker.features.countrylist.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.window.core.layout.WindowSizeClass
import com.tecruz.countrytracker.LocalWindowSizeClass
import com.tecruz.countrytracker.core.designsystem.CountryTrackerTheme
import com.tecruz.countrytracker.features.countrylist.presentation.components.WorldMapCanvas
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * T034: UI test for WorldMapCanvas on medium/large screens.
 * Verifies the world map renders correctly at tablet sizes.
 */
@RunWith(AndroidJUnit4::class)
class WorldMapCanvasTabletTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mediumWindowSizeClass = WindowSizeClass.compute(700f, 900f)
    private val expandedWindowSizeClass = WindowSizeClass.compute(900f, 1200f)

    @Test
    fun worldMap_displaysOnMediumScreen() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides mediumWindowSizeClass) {
                    Box(modifier = Modifier.width(700.dp)) {
                        WorldMapCanvas(
                            visitedCountryCodes = setOf("US", "FR", "JP"),
                            modifier = Modifier.aspectRatio(16f / 9f),
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithTag("world_map").assertIsDisplayed()
    }

    @Test
    fun worldMap_displaysOnExpandedScreen() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides expandedWindowSizeClass) {
                    Box(modifier = Modifier.width(900.dp)) {
                        WorldMapCanvas(
                            visitedCountryCodes = setOf("US", "FR", "JP"),
                            modifier = Modifier.aspectRatio(16f / 9f),
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithTag("world_map").assertIsDisplayed()
    }
}
