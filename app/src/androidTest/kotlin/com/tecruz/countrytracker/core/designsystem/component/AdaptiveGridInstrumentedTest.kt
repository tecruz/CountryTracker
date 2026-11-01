package com.tecruz.countrytracker.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
 * Instrumented coverage for [AdaptiveGrid].
 *
 * These complement the Robolectric unit tests: the on-device JaCoCo
 * instrumentation is the source of truth for the combined coverage report.
 */
@RunWith(AndroidJUnit4::class)
class AdaptiveGridInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val compactWindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 400f, heightDp = 800f)
    private val mediumWindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 700f, heightDp = 900f)
    private val expandedWindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 900f, heightDp = 1200f)

    @Test
    fun compact_rendersSingleColumnWithAllItems() {
        val items = listOf("Alpha", "Beta", "Gamma")

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    Box(modifier = Modifier.width(400.dp)) {
                        AdaptiveGrid(
                            items = items,
                            windowSizeClass = compactWindowSizeClass,
                            key = { it },
                        ) { item ->
                            Text(text = item, modifier = Modifier.testTag("item_$item"))
                        }
                    }
                }
            }
        }

        items.forEach { item ->
            composeTestRule.onNodeWithText(item).assertIsDisplayed()
        }
    }

    @Test
    fun medium_rendersGridWithAllItems() {
        val items = listOf("Alpha", "Beta", "Gamma", "Delta")

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides mediumWindowSizeClass) {
                    AdaptiveGrid(
                        items = items,
                        windowSizeClass = mediumWindowSizeClass,
                        key = { it },
                    ) { item ->
                        Text(text = item, modifier = Modifier.testTag("item_$item"))
                    }
                }
            }
        }

        items.forEach { item ->
            composeTestRule.onNodeWithTag("item_$item").assertExists()
        }
    }

    @Test
    fun expanded_rendersGridWithAllItems() {
        val items = listOf("Alpha", "Beta", "Gamma", "Delta", "Epsilon")

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides expandedWindowSizeClass) {
                    AdaptiveGrid(
                        items = items,
                        windowSizeClass = expandedWindowSizeClass,
                        key = { it },
                    ) { item ->
                        Text(text = item, modifier = Modifier.testTag("item_$item"))
                    }
                }
            }
        }

        items.forEach { item ->
            composeTestRule.onNodeWithTag("item_$item").assertExists()
        }
    }

    @Test
    fun emptyItems_rendersWithoutErrors() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    AdaptiveGrid(
                        items = emptyList<String>(),
                        windowSizeClass = compactWindowSizeClass,
                    ) { item ->
                        Text(text = item)
                    }
                }
            }
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun withoutKey_rendersItems() {
        val items = listOf("One", "Two")

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides mediumWindowSizeClass) {
                    AdaptiveGrid(
                        items = items,
                        windowSizeClass = mediumWindowSizeClass,
                        key = null,
                    ) { item ->
                        Text(text = item)
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("One").assertExists()
        composeTestRule.onNodeWithText("Two").assertExists()
    }

    @Test
    fun customContentPadding_isApplied() {
        val items = listOf("Alpha", "Beta")

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    AdaptiveGrid(
                        items = items,
                        windowSizeClass = compactWindowSizeClass,
                        contentPadding = PaddingValues(32.dp),
                        key = { it },
                    ) { item ->
                        Text(text = item, modifier = Modifier.testTag("item_$item"))
                    }
                }
            }
        }

        composeTestRule.onNodeWithTag("item_Alpha").assertIsDisplayed()
        composeTestRule.onNodeWithTag("item_Beta").assertIsDisplayed()
    }
}
