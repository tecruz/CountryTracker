package com.tecruz.countrytracker.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented coverage for [AdaptiveScaffold].
 *
 * Covers the compact single-pane branch as well as the medium/expanded
 * two-pane branch, with and without a navigation rail.
 */
@RunWith(AndroidJUnit4::class)
class AdaptiveScaffoldInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val compactWindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 400f, heightDp = 800f)
    private val mediumWindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 700f, heightDp = 900f)
    private val expandedWindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 900f, heightDp = 1200f)

    @Test
    fun compact_showsContentAndHidesNavigationRail() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    AdaptiveScaffold(
                        windowSizeClass = compactWindowSizeClass,
                        navigationRail = { Text("Nav Rail") },
                    ) {
                        Text("Main Content")
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Main Content").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nav Rail").assertDoesNotExist()
    }

    @Test
    fun medium_showsRailAndContent() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides mediumWindowSizeClass) {
                    AdaptiveScaffold(
                        windowSizeClass = mediumWindowSizeClass,
                        navigationRail = { Text("Nav Rail") },
                    ) {
                        Text("Main Content")
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Main Content").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nav Rail").assertIsDisplayed()
    }

    @Test
    fun expanded_showsRailAndContent() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides expandedWindowSizeClass) {
                    AdaptiveScaffold(
                        windowSizeClass = expandedWindowSizeClass,
                        navigationRail = { Text("Nav Rail") },
                    ) {
                        Text("Main Content")
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Main Content").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nav Rail").assertIsDisplayed()
    }

    @Test
    fun mediumWithoutRail_showsContentOnly() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides mediumWindowSizeClass) {
                    Box(modifier = Modifier.width(700.dp)) {
                        AdaptiveScaffold(
                            windowSizeClass = mediumWindowSizeClass,
                            navigationRail = null,
                        ) {
                            Text("Main Content")
                        }
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Main Content").assertIsDisplayed()
    }
}
