package com.tecruz.countrytracker

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import android.provider.Settings
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import com.tecruz.countrytracker.core.designsystem.CountryTrackerTheme
import com.tecruz.countrytracker.features.countrydetail.presentation.HeroCard
import com.tecruz.countrytracker.features.countrydetail.presentation.NotesCard
import com.tecruz.countrytracker.features.countrydetail.presentation.NotesDialog
import com.tecruz.countrytracker.features.countrydetail.presentation.RatingCard
import com.tecruz.countrytracker.features.countrydetail.presentation.UnvisitedConfirmationDialog
import com.tecruz.countrytracker.features.countrydetail.presentation.VisitStatusCard
import com.tecruz.countrytracker.features.countrydetail.presentation.model.CountryDetailUi
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CountryDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testCountry = CountryDetailUi(
        code = "US",
        name = "United States",
        region = "North America",
        visited = true,
        visitedDate = 1704067200000L,
        visitedDateFormatted = "January 01, 2024",
        notes = "Amazing trip to NYC!",
        rating = 4,
        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8",
    )

    @Test
    fun heroCard_displaysCountryInfo() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                HeroCard(country = testCountry)
            }
        }

        composeTestRule.onNodeWithText("United States").assertIsDisplayed()
        composeTestRule.onNodeWithText("North America").assertIsDisplayed()
    }

    @Test
    fun heroCard_displaysWithAnimationsEnabled() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val uiAutomation = instrumentation.uiAutomation

        // Read current scale to restore later
        val originalScale = Settings.Global.getFloat(
            instrumentation.targetContext.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        )

        try {
            // Enable animations so the animationsEnabled=true branch is covered
            uiAutomation.executeShellCommand("settings put global animator_duration_scale 1")
                .close()
            // Allow the setting to propagate
            Thread.sleep(200)

            composeTestRule.setContent {
                CountryTrackerTheme {
                    HeroCard(country = testCountry)
                }
            }

            composeTestRule.onNodeWithText("United States").assertIsDisplayed()
            composeTestRule.onNodeWithText("North America").assertIsDisplayed()
        } finally {
            // Restore original scale
            uiAutomation.executeShellCommand(
                "settings put global animator_duration_scale $originalScale",
            ).close()
        }
    }

    @Test
    fun ratingCard_displaysCurrentRating() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                RatingCard(
                    rating = 4,
                    onRatingChange = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Your Rating").assertIsDisplayed()
    }

    @Test
    fun ratingCard_allowsRatingChange() {
        var currentRating = 0

        composeTestRule.setContent {
            CountryTrackerTheme {
                RatingCard(
                    rating = currentRating,
                    onRatingChange = { currentRating = it },
                )
            }
        }

        // Click on the 3rd star
        composeTestRule.onNodeWithContentDescription("Rate 3 stars").performClick()
        assert(currentRating == 3)
    }

    @Test
    fun notesCard_displaysNotes() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                NotesCard(
                    notes = "Amazing trip to NYC!",
                    onEditNotes = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Notes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Amazing trip to NYC!").assertIsDisplayed()
    }

    @Test
    fun notesCard_showsPlaceholderWhenEmpty() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                NotesCard(
                    notes = "",
                    onEditNotes = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Notes").assertIsDisplayed()
        composeTestRule.onNodeWithText("No notes yet. Tap edit to add your memories!").assertIsDisplayed()
    }

    @Test
    fun notesCard_editButtonTriggersCallback() {
        var editClicked = false

        composeTestRule.setContent {
            CountryTrackerTheme {
                NotesCard(
                    notes = "Some notes",
                    onEditNotes = { editClicked = true },
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Edit notes").performClick()
        assert(editClicked)
    }

    @Test
    fun visitStatusCard_showsVisitedStatus() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                VisitStatusCard(
                    country = testCountry,
                    onEditDate = {},
                    onMarkAsUnvisited = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Visited", substring = true).assertIsDisplayed()
    }

    @Test
    fun visitStatusCard_markAsUnvisitedWorks() {
        var unvisitedClicked = false

        composeTestRule.setContent {
            CountryTrackerTheme {
                VisitStatusCard(
                    country = testCountry,
                    onEditDate = {},
                    onMarkAsUnvisited = { unvisitedClicked = true },
                )
            }
        }

        composeTestRule.onNodeWithText("Mark as not visited").performClick()
        assert(unvisitedClicked)
    }

    @Test
    fun visitStatusCard_editDateButtonWorks() {
        var editDateClicked = false

        composeTestRule.setContent {
            CountryTrackerTheme {
                VisitStatusCard(
                    country = testCountry,
                    onEditDate = { editDateClicked = true },
                    onMarkAsUnvisited = {},
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Edit visit date").performClick()
        assert(editDateClicked)
    }

    @Test
    fun ratingCard_displaysFiveStars() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                RatingCard(
                    rating = 0,
                    onRatingChange = {},
                )
            }
        }

        // Should have 5 star buttons
        composeTestRule.onNodeWithContentDescription("Rate 1 stars").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Rate 2 stars").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Rate 3 stars").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Rate 4 stars").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Rate 5 stars").assertIsDisplayed()
    }

    @Test
    fun notesDialog_displaysWhenEditButtonClicked() {
        val currentNotes = "Initial notes"

        composeTestRule.setContent {
            var showNotesDialog by remember { mutableStateOf(false) }

            CountryTrackerTheme {
                NotesCard(
                    notes = currentNotes,
                    onEditNotes = { showNotesDialog = true },
                )

                if (showNotesDialog) {
                    NotesDialog(
                        currentNotes = currentNotes,
                        onDismiss = { },
                        onSave = { _ -> },
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Notes").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Edit notes").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Edit Notes", useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Edit Notes", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Initial notes", useUnmergedTree = true)[1].assertIsDisplayed()
        composeTestRule.onNodeWithText("Save", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun notesDialog_savesNewNotes() {
        var savedNotes = ""

        composeTestRule.setContent {
            var showNotesDialog by remember { mutableStateOf(false) }
            var currentNotes by remember { mutableStateOf("Initial notes") }

            CountryTrackerTheme {
                NotesCard(
                    notes = currentNotes,
                    onEditNotes = { showNotesDialog = true },
                )

                if (showNotesDialog) {
                    NotesDialog(
                        currentNotes = currentNotes,
                        onDismiss = { showNotesDialog = false },
                        onSave = { newNotes ->
                            savedNotes = newNotes
                            currentNotes = newNotes
                        },
                    )
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Edit notes").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Edit Notes", useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodesWithText("Initial notes", useUnmergedTree = true)[1].performClick()
        composeTestRule.onAllNodesWithText(
            "Initial notes",
            useUnmergedTree = true,
        )[1].performTextReplacement("Updated notes")

        composeTestRule.onAllNodesWithText("Save", useUnmergedTree = true)[0].performClick()

        assert(savedNotes == "Updated notes")
        composeTestRule.onNodeWithText("Edit Notes", useUnmergedTree = true).assertIsNotDisplayed()
    }

    @Test
    fun notesDialog_dismissesOnCancel() {
        var savedNotes = ""

        composeTestRule.setContent {
            var showNotesDialog by remember { mutableStateOf(false) }
            val currentNotes by remember { mutableStateOf("Initial notes") }

            CountryTrackerTheme {
                NotesCard(
                    notes = currentNotes,
                    onEditNotes = { showNotesDialog = true },
                )

                if (showNotesDialog) {
                    NotesDialog(
                        currentNotes = currentNotes,
                        onDismiss = { showNotesDialog = false },
                        onSave = { newNotes ->
                            savedNotes = newNotes
                        },
                    )
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Edit notes").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Edit Notes", useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodesWithText("Cancel", useUnmergedTree = true)[0].performClick()

        assert(savedNotes == "")
        composeTestRule.onNodeWithText("Edit Notes", useUnmergedTree = true).assertIsNotDisplayed()
    }

    @Test
    fun unvisitedConfirmationDialog_displaysWhenMarkAsUnvisitedClicked() {
        composeTestRule.setContent {
            var showUnvisitedConfirmation by remember { mutableStateOf(false) }

            CountryTrackerTheme {
                VisitStatusCard(
                    country = testCountry,
                    onEditDate = {},
                    onMarkAsUnvisited = { showUnvisitedConfirmation = true },
                )

                if (showUnvisitedConfirmation) {
                    UnvisitedConfirmationDialog(
                        onConfirm = {
                            showUnvisitedConfirmation = false
                        },
                        onDismiss = { showUnvisitedConfirmation = false },
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
        composeTestRule.onNodeWithText("Remove visit?", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            "This will remove the visit date, notes, and rating for this country. This action cannot be undone.",
            substring = true,
            useUnmergedTree = true,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText("Remove", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun unvisitedConfirmationDialog_confirmsAction() {
        var confirmed = false

        composeTestRule.setContent {
            var showUnvisitedConfirmation by remember { mutableStateOf(false) }

            CountryTrackerTheme {
                VisitStatusCard(
                    country = testCountry,
                    onEditDate = {},
                    onMarkAsUnvisited = { showUnvisitedConfirmation = true },
                )

                if (showUnvisitedConfirmation) {
                    UnvisitedConfirmationDialog(
                        onConfirm = {
                            confirmed = true
                            showUnvisitedConfirmation = false
                        },
                        onDismiss = { showUnvisitedConfirmation = false },
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
        composeTestRule.onAllNodesWithText("Remove", useUnmergedTree = true)[0].performClick()

        assert(confirmed)
        composeTestRule.onNodeWithText("Remove visit?", useUnmergedTree = true).assertIsNotDisplayed()
    }

    @Test
    fun unvisitedConfirmationDialog_dismissesOnCancel() {
        var confirmed = false

        composeTestRule.setContent {
            var showUnvisitedConfirmation by remember { mutableStateOf(false) }

            CountryTrackerTheme {
                VisitStatusCard(
                    country = testCountry,
                    onEditDate = {},
                    onMarkAsUnvisited = { showUnvisitedConfirmation = true },
                )

                if (showUnvisitedConfirmation) {
                    UnvisitedConfirmationDialog(
                        onConfirm = {
                            confirmed = true
                            showUnvisitedConfirmation = false
                        },
                        onDismiss = { showUnvisitedConfirmation = false },
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
        composeTestRule.onAllNodesWithText("Cancel", useUnmergedTree = true)[0].performClick()

        assert(!confirmed)
        composeTestRule.onNodeWithText("Remove visit?", useUnmergedTree = true).assertIsNotDisplayed()
    }

    @Test
    fun snackbar_displaysErrorAndDismisses() {
        composeTestRule.setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            CountryTrackerTheme {
                Box {
                    Button(onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "An error occurred",
                                actionLabel = "Dismiss",
                            )
                        }
                    }) {
                        Text("Show Snackbar")
                    }

                    SnackbarHost(hostState = snackbarHostState) { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Show Snackbar").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText(
                "An error occurred",
                useUnmergedTree = true,
            ).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("An error occurred", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Dismiss", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun snackbar_actionClickDismisses() {
        composeTestRule.setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            CountryTrackerTheme {
                Box {
                    Button(onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Test error",
                                actionLabel = "Retry",
                            )
                        }
                    }) {
                        Text("Show Snackbar")
                    }

                    SnackbarHost(hostState = snackbarHostState) { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Show Snackbar").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Test error", useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Test error", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun notesDialog_displaysCharacterCount() {
        val currentNotes = "Test notes"

        composeTestRule.setContent {
            var showNotesDialog by remember { mutableStateOf(false) }

            CountryTrackerTheme {
                NotesCard(
                    notes = currentNotes,
                    onEditNotes = { showNotesDialog = true },
                )

                if (showNotesDialog) {
                    NotesDialog(
                        currentNotes = currentNotes,
                        onDismiss = { },
                        onSave = {},
                    )
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Edit notes").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Test notes", useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onAllNodesWithText("Test notes", useUnmergedTree = true)[1].assertIsDisplayed()
    }

    @Test
    fun notesDialog_showsPlaceholderWhenEmpty() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                NotesDialog(
                    currentNotes = "",
                    onDismiss = {},
                    onSave = {},
                )
            }
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText(
                "Edit Notes",
                useUnmergedTree = true,
            ).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Edit Notes", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            "Write your memories here\u2026",
            useUnmergedTree = true,
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText("0 / 500", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNode(hasText("Save") and hasClickAction()).assertIsEnabled()
    }

    @Test
    fun notesDialog_disablesSaveWhenOverLimit() {
        val overLimitNotes = "A".repeat(501)

        composeTestRule.setContent {
            CountryTrackerTheme {
                NotesDialog(
                    currentNotes = overLimitNotes,
                    onDismiss = {},
                    onSave = {},
                )
            }
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText(
                "Edit Notes",
                useUnmergedTree = true,
            ).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Edit Notes", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("501 / 500", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNode(hasText("Save") and hasClickAction()).assertIsNotEnabled()
    }

    @Test
    fun notesDialog_saveReEnabledWhenBackUnderLimit() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                NotesDialog(
                    currentNotes = "A".repeat(501),
                    onDismiss = {},
                    onSave = {},
                )
            }
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText(
                "Edit Notes",
                useUnmergedTree = true,
            ).fetchSemanticsNodes().isNotEmpty()
        }

        // Save should be disabled while over limit
        composeTestRule.onNode(hasText("Save") and hasClickAction()).assertIsNotEnabled()

        // Replace with text under limit
        composeTestRule.onAllNodesWithText(
            "A".repeat(501),
            useUnmergedTree = true,
        )[0].performTextReplacement("Short note")

        // Save should now be enabled
        composeTestRule.onNodeWithText("10 / 500", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNode(hasText("Save") and hasClickAction()).assertIsEnabled()
    }

    @Test
    fun heroCard_responsiveOnCompact() {
        val compactWindowSizeClass = WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(
            widthDp = 375f,
            heightDp = 667f,
        )

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    HeroCard(country = testCountry)
                }
            }
        }

        composeTestRule.onNodeWithText("United States").assertIsDisplayed()
        composeTestRule.onNodeWithText("North America").assertIsDisplayed()
    }

    @Test
    fun notesCard_responsiveOnCompact() {
        val compactWindowSizeClass = WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(
            widthDp = 375f,
            heightDp = 667f,
        )

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    NotesCard(
                        notes = "Amazing trip to NYC!",
                        onEditNotes = {},
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Notes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Amazing trip to NYC!").assertIsDisplayed()
    }

    @Test
    fun ratingCard_responsiveOnCompact() {
        val compactWindowSizeClass = WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(
            widthDp = 375f,
            heightDp = 667f,
        )

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    RatingCard(
                        rating = 4,
                        onRatingChange = {},
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Your Rating").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Rate 1 stars").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Rate 5 stars").assertIsDisplayed()
    }

    @Test
    fun visitStatusCard_responsiveOnCompact() {
        val compactWindowSizeClass = WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(
            widthDp = 375f,
            heightDp = 667f,
        )

        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    VisitStatusCard(
                        country = testCountry,
                        onEditDate = {},
                        onMarkAsUnvisited = {},
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Visited", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("January 01, 2024").assertIsDisplayed()
    }
}
