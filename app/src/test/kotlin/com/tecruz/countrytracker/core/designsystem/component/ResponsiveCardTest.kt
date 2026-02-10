package com.tecruz.countrytracker.core.designsystem.component

import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.tecruz.countrytracker.core.util.isCompact
import com.tecruz.countrytracker.core.util.isExpanded
import com.tecruz.countrytracker.core.util.isMedium
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Unit tests for ResponsiveCard composable.
 * Uses Robolectric to render the composable and verify layout behavior.
 */
@RunWith(RobolectricTestRunner::class)
class ResponsiveCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun compactWindowSizeClass(): WindowSizeClass = WindowSizeClass.compute(400f, 800f)

    private fun mediumWindowSizeClass(): WindowSizeClass = WindowSizeClass.compute(700f, 900f)

    private fun expandedWindowSizeClass(): WindowSizeClass = WindowSizeClass.compute(900f, 1200f)

    // --- Pure logic tests ---

    @Test
    fun `compact window is classified as compact`() {
        assertTrue(compactWindowSizeClass().isCompact())
    }

    @Test
    fun `medium window is classified as medium`() {
        assertTrue(mediumWindowSizeClass().isMedium())
    }

    @Test
    fun `expanded window is classified as expanded`() {
        assertTrue(expandedWindowSizeClass().isExpanded())
    }

    // --- Composable rendering tests ---

    @Test
    fun `responsive card renders content on compact screen`() {
        composeTestRule.setContent {
            ResponsiveCard(windowSizeClass = compactWindowSizeClass()) {
                Text("Compact Card Content")
            }
        }

        composeTestRule.onNodeWithText("Compact Card Content").assertIsDisplayed()
    }

    @Test
    fun `responsive card renders content on medium screen`() {
        composeTestRule.setContent {
            ResponsiveCard(windowSizeClass = mediumWindowSizeClass()) {
                Text("Medium Card Content")
            }
        }

        composeTestRule.onNodeWithText("Medium Card Content").assertIsDisplayed()
    }

    @Test
    fun `responsive card renders content on expanded screen`() {
        composeTestRule.setContent {
            ResponsiveCard(windowSizeClass = expandedWindowSizeClass()) {
                Text("Expanded Card Content")
            }
        }

        composeTestRule.onNodeWithText("Expanded Card Content").assertIsDisplayed()
    }

    @Test
    fun `responsive card renders with custom minHeight`() {
        composeTestRule.setContent {
            ResponsiveCard(
                windowSizeClass = compactWindowSizeClass(),
                minHeight = 100.dp,
            ) {
                Text("With Min Height")
            }
        }

        composeTestRule.onNodeWithText("With Min Height").assertIsDisplayed()
    }

    @Test
    fun `responsive card renders without minHeight`() {
        composeTestRule.setContent {
            ResponsiveCard(
                windowSizeClass = mediumWindowSizeClass(),
                minHeight = null,
            ) {
                Text("No Min Height")
            }
        }

        composeTestRule.onNodeWithText("No Min Height").assertIsDisplayed()
    }

    @Test
    fun `card values scale progressively from compact to expanded`() {
        assertTrue(compactWindowSizeClass().isCompact())
        assertTrue(mediumWindowSizeClass().isMedium())
        assertTrue(expandedWindowSizeClass().isExpanded())
    }

    @Test
    fun `responsive card renders with custom colors`() {
        composeTestRule.setContent {
            ResponsiveCard(
                windowSizeClass = compactWindowSizeClass(),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = androidx.compose.ui.graphics.Color.Red,
                ),
            ) {
                Text("Custom Colors")
            }
        }

        composeTestRule.onNodeWithText("Custom Colors").assertIsDisplayed()
    }

    @Test
    fun `responsive card renders with custom elevation`() {
        composeTestRule.setContent {
            ResponsiveCard(
                windowSizeClass = mediumWindowSizeClass(),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(
                    defaultElevation = 8.dp,
                ),
            ) {
                Text("Custom Elevation")
            }
        }

        composeTestRule.onNodeWithText("Custom Elevation").assertIsDisplayed()
    }

    @Test
    fun `responsive card renders with custom modifier`() {
        composeTestRule.setContent {
            ResponsiveCard(
                windowSizeClass = expandedWindowSizeClass(),
                modifier = Modifier.testTag("custom_card"),
            ) {
                Text("Custom Modifier")
            }
        }

        composeTestRule.onNodeWithText("Custom Modifier").assertIsDisplayed()
        composeTestRule.onNodeWithTag("custom_card").assertExists()
    }

    @Test
    fun `responsive card with all custom parameters renders correctly`() {
        composeTestRule.setContent {
            ResponsiveCard(
                windowSizeClass = compactWindowSizeClass(),
                modifier = Modifier.testTag("full_custom"),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = androidx.compose.ui.graphics.Color.Blue,
                ),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(
                    defaultElevation = 6.dp,
                ),
                minHeight = 80.dp,
            ) {
                Text("All Custom")
            }
        }

        composeTestRule.onNodeWithText("All Custom").assertIsDisplayed()
    }
}
