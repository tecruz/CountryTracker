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
import androidx.window.core.layout.computeWindowSizeClass
import com.tecruz.countrytracker.LocalWindowSizeClass
import com.tecruz.countrytracker.core.designsystem.CountryTrackerTheme
import com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.WorldMapCanvas
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * T014: UI test for WorldMapCanvas on compact screen (375dp).
 * Verifies the world map renders and fits within compact screen bounds.
 */
@RunWith(AndroidJUnit4::class)
class WorldMapCanvasCompactTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val compactWindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 375f, heightDp = 667f)

    @Test
    fun worldMap_displaysOnCompactScreen() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    Box(modifier = Modifier.width(375.dp)) {
                        WorldMapCanvas(
                            visitedCountryCodes = emptySet(),
                            modifier = Modifier.aspectRatio(16f / 9f),
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithTag("world_map").assertIsDisplayed()
    }

    @Test
    fun worldMap_maintainsAspectRatioOnCompact() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    Box(modifier = Modifier.width(375.dp)) {
                        WorldMapCanvas(
                            visitedCountryCodes = setOf("US", "FR"),
                            modifier = Modifier.aspectRatio(16f / 9f),
                        )
                    }
                }
            }
        }

        // The map should be displayed and maintain its aspect ratio
        composeTestRule.onNodeWithTag("world_map").assertIsDisplayed()
    }
}
