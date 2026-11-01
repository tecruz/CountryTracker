package com.tecruz.countrytracker.core.designsystem

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented coverage for [CountryTrackerTheme].
 *
 * Exercises the dynamic, dark and light colour-scheme branches.
 */
@RunWith(AndroidJUnit4::class)
class ThemeInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun lightStaticTheme_rendersContent() {
        composeTestRule.setContent {
            CountryTrackerTheme(darkTheme = false, dynamicColor = false) {
                Text("Light content")
            }
        }

        composeTestRule.onNodeWithText("Light content").assertIsDisplayed()
    }

    @Test
    fun darkStaticTheme_rendersContent() {
        composeTestRule.setContent {
            CountryTrackerTheme(darkTheme = true, dynamicColor = false) {
                Text("Dark content")
            }
        }

        composeTestRule.onNodeWithText("Dark content").assertIsDisplayed()
    }

    @Test
    fun dynamicTheme_rendersContent() {
        composeTestRule.setContent {
            CountryTrackerTheme(dynamicColor = true) {
                Text("Dynamic content")
            }
        }

        composeTestRule.onNodeWithText("Dynamic content").assertIsDisplayed()
    }
}
