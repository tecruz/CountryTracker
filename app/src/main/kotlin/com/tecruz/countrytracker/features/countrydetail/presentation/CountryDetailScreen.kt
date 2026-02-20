package com.tecruz.countrytracker.features.countrydetail.presentation

import android.provider.Settings
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecruz.countrytracker.LocalWindowSizeClass
import com.tecruz.countrytracker.R
import com.tecruz.countrytracker.core.designsystem.Background
import com.tecruz.countrytracker.core.designsystem.HeroCardGradientEnd
import com.tecruz.countrytracker.core.designsystem.OnSurface
import com.tecruz.countrytracker.core.designsystem.OnSurfaceVariant
import com.tecruz.countrytracker.core.designsystem.PrimaryContainer
import com.tecruz.countrytracker.core.designsystem.PrimaryGreen
import com.tecruz.countrytracker.core.designsystem.SecondaryContainer
import com.tecruz.countrytracker.core.designsystem.StarYellow
import com.tecruz.countrytracker.core.designsystem.Surface
import com.tecruz.countrytracker.core.designsystem.VisitStatusGradientEnd
import com.tecruz.countrytracker.core.designsystem.preview.DevicePreviews
import com.tecruz.countrytracker.core.designsystem.preview.ExcludeFromGeneratedCoverageReport
import com.tecruz.countrytracker.core.designsystem.preview.PreviewCountryDetails
import com.tecruz.countrytracker.core.designsystem.preview.PreviewWrapper
import com.tecruz.countrytracker.core.util.contentPadding
import com.tecruz.countrytracker.core.util.itemSpacing
import com.tecruz.countrytracker.features.countrydetail.domain.model.CountryDetail
import com.tecruz.countrytracker.features.countrydetail.presentation.model.CountryDetailUi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CountryDetailScreen(onNavigateBack: () -> Unit, viewModel: CountryDetailViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()
    // Use rememberSaveable to survive process death
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showNotesDialog by rememberSaveable { mutableStateOf(false) }
    var showUnvisitedConfirmation by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val errorDismissText = stringResource(R.string.error_dismiss)
    val windowSizeClass = LocalWindowSizeClass.current
    val cPadding = windowSizeClass.contentPadding()
    val iSpacing = windowSizeClass.itemSpacing()

    val country = uiState.country

    // Show error snackbar when error occurs
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                actionLabel = errorDismissText,
            )
            viewModel.clearError()
        }
    }

    if (uiState.isLoading || country == null) {
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
                    onRatingChange = viewModel::updateRating,
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
                    enabled = !uiState.isSaving,
                ) {
                    if (uiState.isSaving) {
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
                                viewModel.markAsVisited(date, "", 0)
                            } else {
                                viewModel.markAsVisited(date, country.notes, country.rating)
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
                viewModel.markAsUnvisited()
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
            onSave = viewModel::updateNotes,
        )
    }
}

@Composable
fun NotesDialog(currentNotes: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var tempNotes by remember { mutableStateOf(currentNotes) }
    val maxLength = CountryDetail.MAX_NOTES_LENGTH
    val isOverLimit = tempNotes.length > maxLength

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_notes)) },
        text = {
            Column {
                OutlinedTextField(
                    value = tempNotes,
                    onValueChange = { newValue ->
                        tempNotes = newValue
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp),
                    placeholder = { Text(stringResource(R.string.notes_placeholder)) },
                    minLines = 5,
                    maxLines = 10,
                    shape = RoundedCornerShape(12.dp),
                    isError = isOverLimit,
                    supportingText = {
                        Text(
                            text = stringResource(R.string.notes_character_count, tempNotes.length, maxLength),
                            color = if (isOverLimit) MaterialTheme.colorScheme.error else OnSurfaceVariant,
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isOverLimit) MaterialTheme.colorScheme.error else PrimaryGreen,
                        errorBorderColor = MaterialTheme.colorScheme.error,
                    ),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(tempNotes)
                    onDismiss()
                },
                enabled = !isOverLimit,
            ) {
                Text(
                    stringResource(R.string.save),
                    color = if (isOverLimit) OnSurfaceVariant else PrimaryGreen,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        containerColor = Surface,
    )
}

@Composable
fun UnvisitedConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.confirm_unvisit_title)) },
        text = { Text(stringResource(R.string.confirm_unvisit_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    stringResource(R.string.confirm_unvisit_action),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        containerColor = Surface,
    )
}

@Composable
fun HeroCard(country: CountryDetailUi, modifier: Modifier = Modifier) {
    // Check if animations are enabled for accessibility
    val context = LocalContext.current
    val animationsEnabled = remember {
        try {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
            ) != 0f
        } catch (_: Settings.SettingNotFoundException) {
            true
        }
    }

    val pulseAlpha = if (animationsEnabled) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val animatedAlpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 0.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(4000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "pulseAlpha",
        )
        animatedAlpha
    } else {
        0.25f
    }

    Card(
        modifier = modifier.height(240.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(SecondaryContainer, HeroCardGradientEnd),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            // Pulsing glow effect
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = pulseAlpha),
                                Color.Transparent,
                            ),
                        ),
                    ),
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = country.flagEmoji,
                    fontSize = 120.sp,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = country.name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnSurface,
                )
                Text(
                    text = country.region,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = OnSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun VisitStatusCard(
    country: CountryDetailUi,
    onEditDate: () -> Unit,
    onMarkAsUnvisited: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(PrimaryContainer, VisitStatusGradientEnd),
                    ),
                )
                .padding(16.dp),
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "✓ ${stringResource(R.string.visited_status)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                    )
                    TextButton(onClick = onMarkAsUnvisited) {
                        Text(stringResource(R.string.mark_as_not_visited), color = PrimaryGreen)
                    }
                }

                if (country.visitedDateFormatted != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = PrimaryGreen,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = country.visitedDateFormatted,
                                fontSize = 16.sp,
                                color = OnSurface,
                            )
                        }
                        IconButton(onClick = onEditDate) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(R.string.content_desc_edit_date),
                                tint = PrimaryGreen,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RatingCard(rating: Int, onRatingChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.your_rating),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                for (i in 1..5) {
                    IconButton(
                        onClick = { onRatingChange(i) },
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = stringResource(R.string.content_desc_rate_stars, i),
                            tint = StarYellow,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotesCard(notes: String, onEditNotes: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.notes_title),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                )
                IconButton(onClick = onEditNotes) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.content_desc_edit_notes),
                        tint = PrimaryGreen,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (notes.isNotEmpty()) {
                Text(
                    text = notes,
                    fontSize = 14.sp,
                    color = OnSurface,
                    lineHeight = 20.sp,
                )
            } else {
                Text(
                    text = stringResource(R.string.no_notes_placeholder),
                    fontSize = 14.sp,
                    color = OnSurfaceVariant,
                    lineHeight = 20.sp,
                )
            }
        }
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
