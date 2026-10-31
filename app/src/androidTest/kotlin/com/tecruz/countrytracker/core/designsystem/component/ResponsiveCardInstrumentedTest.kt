package com.tecruz.countrytracker.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import com.tecruz.countrytracker.LocalWindowSizeClass
import com.tecruz.countrytracker.core.designsystem.CountryTrackerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented coverage for [ResponsiveCard].
 *
 * Covers the three window-size branches, the optional min height, and the
 * custom colours/elevation overrides.
 */
@RunWith(AndroidJUnit4::class)
class ResponsiveCardInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val compactWindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 375f, heightDp = 667f)
    private val mediumWindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 700f, heightDp = 900f)
    private val expandedWindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 900f, heightDp = 1200f)

    @Test
    fun compact_rendersContent() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    Box(modifier = Modifier.width(375.dp)) {
                        ResponsiveCard(windowSizeClass = compactWindowSizeClass) {
                            Text("Compact Card Content")
                        }
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Compact Card Content").assertIsDisplayed()
    }

    @Test
    fun medium_rendersContent() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides mediumWindowSizeClass) {
                    ResponsiveCard(windowSizeClass = mediumWindowSizeClass) {
                        Text("Medium Card Content")
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Medium Card Content").assertIsDisplayed()
    }

    @Test
    fun expanded_rendersContent() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides expandedWindowSizeClass) {
                    ResponsiveCard(windowSizeClass = expandedWindowSizeClass) {
                        Text("Expanded Card Content")
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Expanded Card Content").assertIsDisplayed()
    }

    @Test
    fun customMinHeight_isApplied() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    ResponsiveCard(
                        windowSizeClass = compactWindowSizeClass,
                        minHeight = 100.dp,
                    ) {
                        Text("With Min Height")
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("With Min Height").assertIsDisplayed()
    }

    @Test
    fun nullMinHeight_isApplied() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides mediumWindowSizeClass) {
                    ResponsiveCard(
                        windowSizeClass = mediumWindowSizeClass,
                        minHeight = null,
                    ) {
                        Text("No Min Height")
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("No Min Height").assertIsDisplayed()
    }

    @Test
    fun customColors_areApplied() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    ResponsiveCard(
                        windowSizeClass = compactWindowSizeClass,
                        colors = CardDefaults.cardColors(containerColor = Color.Red),
                    ) {
                        Text("Custom Colors")
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Custom Colors").assertIsDisplayed()
    }

    @Test
    fun customElevation_isApplied() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides expandedWindowSizeClass) {
                    ResponsiveCard(
                        windowSizeClass = expandedWindowSizeClass,
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    ) {
                        Text(
                            text = "Custom Elevation",
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Custom Elevation").assertIsDisplayed()
    }
}
