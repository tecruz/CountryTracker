package com.tecruz.countrytracker.features.countrydetail.presentation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import com.tecruz.countrytracker.LocalWindowSizeClass
import com.tecruz.countrytracker.core.designsystem.CountryTrackerTheme
import com.tecruz.countrytracker.features.countrydetail.presentation.model.CountryDetailUi
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests that cover the dialog if-branches in CountryDetailScreen (lines 259 and 269):
 * - `if (showUnvisitedConfirmation)` -> UnvisitedConfirmationDialog
 * - `if (showNotesDialog)` -> NotesDialog
 */
@RunWith(AndroidJUnit4::class)
class CountryDetailScreenDialogsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val windowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 375f, heightDp = 667f)

    private val visitedCountry = CountryDetailUi(
        code = "US",
        name = "United States",
        region = "North America",
        visited = true,
        visitedDate = 1704067200000L,
        visitedDateFormatted = "January 01, 2024",
        notes = "Amazing trip!",
        rating = 4,
        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8",
    )

    private fun createMockViewModel(country: CountryDetailUi): CountryDetailViewModel {
        val uiStateFlow = MutableStateFlow(
            CountryDetailUiState(
                country = country,
                isLoading = false,
                error = null,
                isSaving = false,
            ),
        )
        return mockk<CountryDetailViewModel>(relaxed = true) {
            every { uiState } returns uiStateFlow
        }
    }

    /**
     * Covers line 259: `if (showUnvisitedConfirmation)`
     * When a visited country is displayed and the user taps "Mark as not visited",
     * the UnvisitedConfirmationDialog should appear.
     */
    @Test
    fun countryDetailScreen_showsUnvisitedConfirmationDialog_whenMarkAsUnvisitedClicked() {
        val viewModel = createMockViewModel(visitedCountry)

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                    CountryDetailScreen(
                        onNavigateBack = {},
                        viewModel = viewModel,
                    )
                }
            }
        }

        // Tap "Mark as not visited" to trigger showUnvisitedConfirmation = true
        composeTestRule.onNodeWithText("Mark as not visited").performClick()

        // Verify the UnvisitedConfirmationDialog is displayed (line 259 branch)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText(
                "Remove visit?",
                useUnmergedTree = true,
            ).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Remove visit?", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Remove", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel", useUnmergedTree = true).assertIsDisplayed()
    }

    /**
     * Covers line 259: `if (showUnvisitedConfirmation)` - confirm action calls viewModel.markAsUnvisited()
     */
    @Test
    fun countryDetailScreen_unvisitedConfirmationDialog_confirmCallsViewModel() {
        val viewModel = createMockViewModel(visitedCountry)

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                    CountryDetailScreen(
                        onNavigateBack = {},
                        viewModel = viewModel,
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Mark as not visited").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText(
                "Remove visit?",
                useUnmergedTree = true,
            ).fetchSemanticsNodes().isNotEmpty()
        }

        // Tap "Remove" to confirm
        composeTestRule.onAllNodesWithText("Remove", useUnmergedTree = true)[0].performClick()

        verify { viewModel.markAsUnvisited() }
    }

    /**
     * Covers line 269: `if (showNotesDialog)`
     * When a visited country is displayed and the user taps the edit notes button,
     * the NotesDialog should appear.
     */
    @Test
    fun countryDetailScreen_showsNotesDialog_whenEditNotesClicked() {
        val viewModel = createMockViewModel(visitedCountry)

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                    CountryDetailScreen(
                        onNavigateBack = {},
                        viewModel = viewModel,
                    )
                }
            }
        }

        // Tap the edit notes button to trigger showNotesDialog = true
        composeTestRule.onNodeWithContentDescription("Edit notes").performClick()

        // Verify the NotesDialog is displayed (line 269 branch)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText(
                "Edit Notes",
                useUnmergedTree = true,
            ).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Edit Notes", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Save", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel", useUnmergedTree = true).assertIsDisplayed()
    }

    /**
     * Covers line 269: `if (showNotesDialog)` - save action calls viewModel.updateNotes()
     */
    @Test
    fun countryDetailScreen_notesDialog_saveCallsViewModel() {
        val viewModel = createMockViewModel(visitedCountry)

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                    CountryDetailScreen(
                        onNavigateBack = {},
                        viewModel = viewModel,
                    )
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Edit notes").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText(
                "Edit Notes",
                useUnmergedTree = true,
            ).fetchSemanticsNodes().isNotEmpty()
        }

        // Tap "Save" to save notes
        composeTestRule.onAllNodesWithText("Save", useUnmergedTree = true)[0].performClick()

        verify { viewModel.updateNotes("Amazing trip!") }
    }
}
