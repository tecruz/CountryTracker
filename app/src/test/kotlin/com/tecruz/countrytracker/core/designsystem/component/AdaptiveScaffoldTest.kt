package com.tecruz.countrytracker.core.designsystem.component

import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.window.core.layout.WindowSizeClass
import com.tecruz.countrytracker.core.util.isCompact
import com.tecruz.countrytracker.core.util.isExpanded
import com.tecruz.countrytracker.core.util.isMedium
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Unit tests for AdaptiveScaffold composable.
 * Uses Robolectric to render the composable and verify layout behavior.
 */
@RunWith(RobolectricTestRunner::class)
class AdaptiveScaffoldTest {

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
    fun `medium window is not compact`() {
        assertFalse(mediumWindowSizeClass().isCompact())
        assertTrue(mediumWindowSizeClass().isMedium())
    }

    @Test
    fun `expanded window is not compact`() {
        assertFalse(expandedWindowSizeClass().isCompact())
        assertTrue(expandedWindowSizeClass().isExpanded())
    }

    // --- Composable rendering tests ---

    @Test
    fun `compact scaffold shows content without navigation rail`() {
        composeTestRule.setContent {
            AdaptiveScaffold(
                windowSizeClass = compactWindowSizeClass(),
                navigationRail = { Text("Nav Rail") },
            ) {
                Text("Main Content")
            }
        }

        composeTestRule.onNodeWithText("Main Content").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nav Rail").assertDoesNotExist()
    }

    @Test
    fun `medium scaffold shows both navigation rail and content`() {
        composeTestRule.setContent {
            AdaptiveScaffold(
                windowSizeClass = mediumWindowSizeClass(),
                navigationRail = { Text("Nav Rail") },
            ) {
                Text("Main Content")
            }
        }

        composeTestRule.onNodeWithText("Main Content").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nav Rail").assertIsDisplayed()
    }

    @Test
    fun `expanded scaffold shows both navigation rail and content`() {
        composeTestRule.setContent {
            AdaptiveScaffold(
                windowSizeClass = expandedWindowSizeClass(),
                navigationRail = { Text("Nav Rail") },
            ) {
                Text("Main Content")
            }
        }

        composeTestRule.onNodeWithText("Main Content").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nav Rail").assertIsDisplayed()
    }

    @Test
    fun `scaffold without navigation rail shows content only on all sizes`() {
        composeTestRule.setContent {
            AdaptiveScaffold(
                windowSizeClass = mediumWindowSizeClass(),
                navigationRail = null,
            ) {
                Text("Content Only")
            }
        }

        composeTestRule.onNodeWithText("Content Only").assertIsDisplayed()
    }

    @Test
    fun `compact scaffold without navigation rail shows content`() {
        composeTestRule.setContent {
            AdaptiveScaffold(
                windowSizeClass = compactWindowSizeClass(),
                navigationRail = null,
            ) {
                Text("Compact No Rail")
            }
        }

        composeTestRule.onNodeWithText("Compact No Rail").assertIsDisplayed()
    }

    @Test
    fun `navigation rail is only shown when not compact and rail is provided`() {
        assertTrue(compactWindowSizeClass().isCompact())
        assertFalse(mediumWindowSizeClass().isCompact())
        assertFalse(expandedWindowSizeClass().isCompact())
    }

    @Test
    fun `adaptive scaffold renders with custom modifier`() {
        composeTestRule.setContent {
            AdaptiveScaffold(
                windowSizeClass = compactWindowSizeClass(),
                modifier = Modifier.testTag("custom_scaffold"),
            ) {
                Text("Scaffold with Modifier")
            }
        }

        composeTestRule.onNodeWithText("Scaffold with Modifier").assertIsDisplayed()
        composeTestRule.onNodeWithTag("custom_scaffold").assertExists()
    }

    @Test
    fun `adaptive scaffold with null nav rail shows content on expanded`() {
        composeTestRule.setContent {
            AdaptiveScaffold(
                windowSizeClass = expandedWindowSizeClass(),
                navigationRail = null,
            ) {
                Text("Expanded No Rail Content")
            }
        }

        composeTestRule.onNodeWithText("Expanded No Rail Content").assertIsDisplayed()
    }

    @Test
    fun `adaptive scaffold without nav rail shows content on medium`() {
        composeTestRule.setContent {
            AdaptiveScaffold(
                windowSizeClass = mediumWindowSizeClass(),
                navigationRail = null,
            ) {
                Text("Medium No Rail Content")
            }
        }

        composeTestRule.onNodeWithText("Medium No Rail Content").assertIsDisplayed()
    }
}
