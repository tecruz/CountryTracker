package com.tecruz.countrytracker.core.presentation

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import com.tecruz.countrytracker.R
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UiTextResolveTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    @After
    fun tearDown() {
        try {
            stopKoin()
        } catch (_: IllegalStateException) {
            // Robolectric may have started Koin from the application.
        }
    }

    @Test
    fun `context resolve covers dynamic text and string resources with and without args`() {
        assertEquals("hello", UiText.DynamicString("hello").resolve(context))
        assertEquals(
            "Could not load the world map",
            UiText.StringResource(R.string.error_load_map).resolve(context),
        )
        assertEquals(
            "3 / 9",
            UiText.StringResource(R.string.notes_character_count, arrayOf(3, 9)).resolve(context),
        )
    }

    @Test
    fun `composable resolve covers a string resource that has arguments`() {
        composeTestRule.setContent {
            Text(text = UiText.StringResource(R.string.notes_character_count, arrayOf(3, 9)).resolve())
        }

        composeTestRule.onNodeWithText("3 / 9").assertExists()
    }
}
