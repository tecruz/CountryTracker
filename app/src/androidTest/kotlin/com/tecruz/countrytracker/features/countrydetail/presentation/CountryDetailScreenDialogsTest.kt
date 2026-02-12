package com.tecruz.countrytracker.features.countrydetail.presentation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import com.tecruz.countrytracker.LocalWindowSizeClass
import com.tecruz.countrytracker.core.designsystem.CountryTrackerTheme
import com.tecruz.countrytracker.features.countrydetail.presentation.model.CountryDetailUi
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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

    private val unvisitedCountry = CountryDetailUi(
        code = "BR",
        name = "Brazil",
        region = "South America",
        visited = false,
        visitedDate = null,
        visitedDateFormatted = null,
        notes = "",
        rating = 0,
        flagEmoji = "\uD83C\uDDE7\uD83C\uDDF7",
    )

    private fun createMockViewModel(
        country: CountryDetailUi,
        isSaving: Boolean = false,
        error: String? = null,
    ): Pair<CountryDetailViewModel, MutableStateFlow<CountryDetailUiState>> {
        val uiStateFlow = MutableStateFlow(
            CountryDetailUiState(
                country = country,
                isLoading = false,
                error = error,
                isSaving = isSaving,
            ),
        )
        val viewModel = mockk<CountryDetailViewModel>(relaxed = true) {
            every { uiState } returns uiStateFlow
        }
        return viewModel to uiStateFlow
    }

    @Test
    fun countryDetailScreen_showsUnvisitedConfirmationDialog_whenMarkAsUnvisitedClicked() {
        val (viewModel, _) = createMockViewModel(visitedCountry)

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
        val (viewModel, _) = createMockViewModel(visitedCountry)

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

    @Test
    fun countryDetailScreen_showsNotesDialog_whenEditNotesClicked() {
        val (viewModel, _) = createMockViewModel(visitedCountry)

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

        // Scroll to and tap the edit notes button to trigger showNotesDialog = true
        composeTestRule.onNodeWithContentDescription("Edit notes").performScrollTo().performClick()

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

    @Test
    fun countryDetailScreen_notesDialog_saveCallsViewModel() {
        val (viewModel, _) = createMockViewModel(visitedCountry)

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

        composeTestRule.onNodeWithContentDescription("Edit notes").performScrollTo().performClick()
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

    /**
     * Covers line 230: `viewModel.markAsVisited(date, country.notes, country.rating)`
     * When a visited country's date is edited via the DatePicker and confirmed with "OK",
     * markAsVisited is called preserving the existing notes and rating.
     */
    @Test
    fun countryDetailScreen_datePickerConfirm_callsMarkAsVisitedWithExistingNotesAndRating() {
        val (viewModel, _) = createMockViewModel(visitedCountry)

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

        // Tap "Edit visit date" on the VisitStatusCard to open the DatePicker
        composeTestRule.onNodeWithContentDescription("Edit visit date").performClick()

        // Wait for the DatePickerDialog to appear
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText(
                "OK",
                useUnmergedTree = true,
            ).fetchSemanticsNodes().isNotEmpty()
        }

        // Tap "OK" to confirm the date selection
        composeTestRule.onNodeWithText("OK", useUnmergedTree = true).performClick()

        // Verify markAsVisited was called with the existing notes and rating
        val dateSlot = slot<Long>()
        verify {
            viewModel.markAsVisited(capture(dateSlot), "Amazing trip!", 4)
        }
        assert(dateSlot.captured > 0L)
    }

    /**
     * Covers lines 197-200: ContainedLoadingIndicator shown inside the
     * "Mark as Visited" button when country is not visited and isSaving is true.
     */
    @Test
    fun countryDetailScreen_showsLoadingIndicator_whenUnvisitedAndSaving() {
        val (viewModel, _) = createMockViewModel(unvisitedCountry, isSaving = true)

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

        // The "Mark as Visited" text should NOT be displayed when saving
        composeTestRule.onNodeWithText("Mark as Visited").assertDoesNotExist()
    }

    /**
     * Covers lines 84-92: LaunchedEffect that shows error snackbar and calls viewModel.clearError().
     * When the uiState has a non-null error, the snackbar is shown with the error message
     * and "Dismiss" action label.
     */
    @Test
    fun countryDetailScreen_showsErrorSnackbar_whenErrorPresent() {
        val (viewModel, _) = createMockViewModel(
            visitedCountry,
            error = "Failed to update notes",
        )

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

        // Wait for the snackbar to appear
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText(
                "Failed to update notes",
                useUnmergedTree = true,
            ).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Failed to update notes", useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Dismiss", useUnmergedTree = true)
            .assertIsDisplayed()

        // Tap "Dismiss" to dismiss the snackbar — clearError() runs after showSnackbar returns
        composeTestRule.onNodeWithText("Dismiss", useUnmergedTree = true).performClick()

        composeTestRule.waitForIdle()

        // Verify clearError was called after the snackbar was dismissed
        verify { viewModel.clearError() }
    }
}
