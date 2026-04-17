package com.tecruz.countrytracker.features.countrydetail.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecruz.countrytracker.LocalWindowSizeClass
import com.tecruz.countrytracker.R
import com.tecruz.countrytracker.core.designsystem.Background
import com.tecruz.countrytracker.core.designsystem.PrimaryGreen
import com.tecruz.countrytracker.core.designsystem.Surface
import com.tecruz.countrytracker.core.designsystem.preview.DevicePreviews
import com.tecruz.countrytracker.core.designsystem.preview.ExcludeFromGeneratedCoverageReport
import com.tecruz.countrytracker.core.designsystem.preview.PreviewCountryDetails
import com.tecruz.countrytracker.core.designsystem.preview.PreviewWrapper
import com.tecruz.countrytracker.core.presentation.asString
import com.tecruz.countrytracker.core.util.contentPadding
import com.tecruz.countrytracker.core.util.itemSpacing
import com.tecruz.countrytracker.features.countrydetail.presentation.components.HeroCard
import com.tecruz.countrytracker.features.countrydetail.presentation.components.NotesCard
import com.tecruz.countrytracker.features.countrydetail.presentation.components.NotesDialog
import com.tecruz.countrytracker.features.countrydetail.presentation.components.RatingCard
import com.tecruz.countrytracker.features.countrydetail.presentation.components.UnvisitedConfirmationDialog
import com.tecruz.countrytracker.features.countrydetail.presentation.components.VisitStatusCard
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CountryDetailRoot(onNavigateBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel: CountryDetailViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    CountryDetailScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CountryDetailScreen(
    state: CountryDetailState,
    onAction: (CountryDetailAction) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Use rememberSaveable to survive process death
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showNotesDialog by rememberSaveable { mutableStateOf(false) }
    var showUnvisitedConfirmation by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val errorDismissText = stringResource(R.string.error_dismiss)
    val windowSizeClass = LocalWindowSizeClass.current
    val cPadding = windowSizeClass.contentPadding()
    val iSpacing = windowSizeClass.itemSpacing()

    val country = state.country

    // Show error snackbar when error occurs
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error.asString(),
                actionLabel = errorDismissText,
            )
            onAction(CountryDetailAction.OnClearError)
        }
    }

    if (state.isLoading || country == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            LoadingIndicator(color = PrimaryGreen)
        }
        return
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        country.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_desc_back),
                            tint = Color.White,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen,
                    titleContentColor = Color.White,
                ),
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        },
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(Background)
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(cPadding),
        ) {
            // Hero Card
            HeroCard(
                country = country,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(cPadding))

            if (country.visited) {
                // Visit Status Card
                VisitStatusCard(
                    country = country,
                    onEditDate = { showDatePicker = true },
                    onMarkAsUnvisited = { showUnvisitedConfirmation = true },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(iSpacing))

                // Rating Card
                RatingCard(
                    rating = country.rating,
                    onRatingChange = { rating -> onAction(CountryDetailAction.OnUpdateRating(rating)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(iSpacing))

                // Notes Card
                NotesCard(
                    notes = country.notes,
                    onEditNotes = { showNotesDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                // Mark as Visited Button
                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen,
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !state.isSaving,
                ) {
                    if (state.isSaving) {
                        ContainedLoadingIndicator(
                            modifier = Modifier.size(24.dp),
                            indicatorColor = Color.White,
                        )
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            stringResource(R.string.mark_as_visited),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = country.visitedDate ?: System.currentTimeMillis(),
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { date ->
                            if (!country.visited) {
                                onAction(CountryDetailAction.OnMarkAsVisited(date, "", 0))
                            } else {
                                onAction(CountryDetailAction.OnMarkAsVisited(date, country.notes, country.rating))
                            }
                        }
                        showDatePicker = false
                    },
                ) {
                    Text(stringResource(R.string.ok), color = PrimaryGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Surface,
            ),
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = PrimaryGreen,
                    todayContentColor = PrimaryGreen,
                    todayDateBorderColor = PrimaryGreen,
                ),
            )
        }
    }

    // Unvisited Confirmation Dialog
    if (showUnvisitedConfirmation) {
        UnvisitedConfirmationDialog(
            onConfirm = {
                onAction(CountryDetailAction.OnMarkAsUnvisited)
                showUnvisitedConfirmation = false
            },
            onDismiss = { showUnvisitedConfirmation = false },
        )
    }

    // Notes Dialog
    if (showNotesDialog) {
        NotesDialog(
            currentNotes = country.notes,
            onDismiss = { showNotesDialog = false },
            onSave = { notes -> onAction(CountryDetailAction.OnUpdateNotes(notes)) },
        )
    }
}

// region Previews

@ExcludeFromGeneratedCoverageReport
@DevicePreviews
@Composable
private fun HeroCardPreview() {
    PreviewWrapper {
        HeroCard(
            country = PreviewCountryDetails.visitedWithNotes,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@ExcludeFromGeneratedCoverageReport
@DevicePreviews
@Composable
private fun VisitStatusCardPreview() {
    PreviewWrapper {
        VisitStatusCard(
            country = PreviewCountryDetails.visitedWithNotes,
            onEditDate = {},
            onMarkAsUnvisited = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@ExcludeFromGeneratedCoverageReport
@DevicePreviews
@Composable
private fun RatingCardPreview() {
    PreviewWrapper {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            RatingCard(rating = 4, onRatingChange = {})
            RatingCard(rating = 0, onRatingChange = {})
        }
    }
}

@ExcludeFromGeneratedCoverageReport
@DevicePreviews
@Composable
private fun NotesCardWithContentPreview() {
    PreviewWrapper {
        NotesCard(
            notes = PreviewCountryDetails.visitedWithNotes.notes,
            onEditNotes = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@ExcludeFromGeneratedCoverageReport
@DevicePreviews
@Composable
private fun NotesCardEmptyPreview() {
    PreviewWrapper {
        NotesCard(
            notes = "",
            onEditNotes = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@ExcludeFromGeneratedCoverageReport
@DevicePreviews
@Composable
private fun NotesDialogPreview() {
    PreviewWrapper {
        NotesDialog(
            currentNotes = "Amazing trip to NYC! The food was incredible and the people were so friendly.",
            onDismiss = {},
            onSave = {},
        )
    }
}

@ExcludeFromGeneratedCoverageReport
@DevicePreviews
@Composable
private fun NotesDialogEmptyPreview() {
    PreviewWrapper {
        NotesDialog(
            currentNotes = "",
            onDismiss = {},
            onSave = {},
        )
    }
}

@ExcludeFromGeneratedCoverageReport
@DevicePreviews
@Composable
private fun UnvisitedConfirmationDialogPreview() {
    PreviewWrapper {
        UnvisitedConfirmationDialog(
            onConfirm = {},
            onDismiss = {},
        )
    }
}

// endregion
