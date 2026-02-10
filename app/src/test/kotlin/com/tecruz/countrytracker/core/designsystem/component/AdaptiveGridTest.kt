package com.tecruz.countrytracker.core.designsystem.component

import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.window.core.layout.WindowSizeClass
import com.tecruz.countrytracker.core.util.gridColumns
import com.tecruz.countrytracker.core.util.itemSpacing
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Unit tests for AdaptiveGrid composable and responsive logic.
 * Uses Robolectric to render Compose UI in unit tests.
 */
@RunWith(RobolectricTestRunner::class)
class AdaptiveGridTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun compactWindowSizeClass(): WindowSizeClass = WindowSizeClass.compute(400f, 800f)

    private fun mediumWindowSizeClass(): WindowSizeClass = WindowSizeClass.compute(700f, 900f)

    private fun expandedWindowSizeClass(): WindowSizeClass = WindowSizeClass.compute(900f, 1200f)

    // --- Pure logic tests ---

    @Test
    fun `compact window uses single column`() {
        assertEquals(1, compactWindowSizeClass().gridColumns())
    }

    @Test
    fun `medium window uses two columns`() {
        assertEquals(2, mediumWindowSizeClass().gridColumns())
    }

    @Test
    fun `expanded window uses three columns`() {
        assertEquals(3, expandedWindowSizeClass().gridColumns())
    }

    @Test
    fun `item spacing increases with screen size`() {
        val compactSpacing = compactWindowSizeClass().itemSpacing()
        val mediumSpacing = mediumWindowSizeClass().itemSpacing()
        val expandedSpacing = expandedWindowSizeClass().itemSpacing()

        assert(compactSpacing < mediumSpacing) {
            "Medium spacing ($mediumSpacing) should be greater than compact ($compactSpacing)"
        }
        assert(mediumSpacing < expandedSpacing) {
            "Expanded spacing ($expandedSpacing) should be greater than medium ($mediumSpacing)"
        }
    }

    // --- Composable rendering tests ---

    @Test
    fun `compact renders LazyColumn with all items`() {
        val items = listOf("Alpha", "Beta", "Gamma")

        composeTestRule.setContent {
            AdaptiveGrid(
                items = items,
                windowSizeClass = compactWindowSizeClass(),
                key = { it },
            ) { item ->
                Text(text = item, modifier = Modifier.testTag("item_$item"))
            }
        }

        items.forEach { item ->
            composeTestRule.onNodeWithText(item).assertIsDisplayed()
        }
    }

    @Test
    fun `medium renders LazyVerticalGrid with all items`() {
        val items = listOf("Alpha", "Beta", "Gamma", "Delta")

        composeTestRule.setContent {
            AdaptiveGrid(
                items = items,
                windowSizeClass = mediumWindowSizeClass(),
                key = { it },
            ) { item ->
                Text(text = item, modifier = Modifier.testTag("item_$item"))
            }
        }

        items.forEach { item ->
            composeTestRule.onNodeWithTag("item_$item").assertExists()
        }
    }

    @Test
    fun `expanded renders LazyVerticalGrid with all items`() {
        val items = listOf("Alpha", "Beta", "Gamma", "Delta", "Epsilon")

        composeTestRule.setContent {
            AdaptiveGrid(
                items = items,
                windowSizeClass = expandedWindowSizeClass(),
                key = { it },
            ) { item ->
                Text(text = item, modifier = Modifier.testTag("item_$item"))
            }
        }

        items.forEach { item ->
            composeTestRule.onNodeWithTag("item_$item").assertExists()
        }
    }

    @Test
    fun `empty items list renders without errors`() {
        composeTestRule.setContent {
            AdaptiveGrid(
                items = emptyList<String>(),
                windowSizeClass = compactWindowSizeClass(),
            ) { item ->
                Text(text = item)
            }
        }

        // Should not crash
        composeTestRule.waitForIdle()
    }

    @Test
    fun `grid without key renders successfully`() {
        val items = listOf("One", "Two")

        composeTestRule.setContent {
            AdaptiveGrid(
                items = items,
                windowSizeClass = mediumWindowSizeClass(),
            ) { item ->
                Text(text = item)
            }
        }

        composeTestRule.onNodeWithText("One").assertExists()
        composeTestRule.onNodeWithText("Two").assertExists()
    }
}
