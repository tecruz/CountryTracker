package com.tecruz.countrytracker.features.countrydetail.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecruz.countrytracker.R
import com.tecruz.countrytracker.core.designsystem.Background
import com.tecruz.countrytracker.core.designsystem.OnSurface
import com.tecruz.countrytracker.core.designsystem.OnSurfaceVariant
import com.tecruz.countrytracker.core.designsystem.PrimaryContainer
import com.tecruz.countrytracker.core.designsystem.PrimaryGreen
import com.tecruz.countrytracker.core.designsystem.SecondaryContainer
import com.tecruz.countrytracker.core.designsystem.StarYellow
import com.tecruz.countrytracker.core.designsystem.Surface
import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.features.countrydetail.domain.UpdateCountryNotesUseCase
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CountryDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: CountryDetailViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showNotesDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val errorDismissText = stringResource(R.string.error_dismiss)

    val country = uiState.country

    // Show error snackbar when error occurs
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                actionLabel = errorDismissText
            )
            viewModel.clearError()
        }
    }

    if (uiState.isLoading || country == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LoadingIndicator(color = PrimaryGreen)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        country.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_desc_back),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen,
                    titleContentColor = Color.White
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(Background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Hero Card
            HeroCard(
                country = country,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            if (country.visited) {
                // Visit Status Card
                VisitStatusCard(
                    country = country,
                    onEditDate = { showDatePicker = true },
                    onMarkAsUnvisited = viewModel::markAsUnvisited,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Rating Card
                RatingCard(
                    rating = country.rating,
                    onRatingChange = viewModel::updateRating,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Notes Card
                NotesCard(
                    notes = country.notes,
                    onEditNotes = { showNotesDialog = true },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Mark as Visited Button
                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !uiState.isSaving
                ) {
                    if (uiState.isSaving) {
                        ContainedLoadingIndicator(
                            modifier = Modifier.size(24.dp),
                            indicatorColor = Color.White
                        )
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            stringResource(R.string.mark_as_visited),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = country.visitedDate ?: System.currentTimeMillis()
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
                    }
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
                containerColor = Surface
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = PrimaryGreen,
                    todayContentColor = PrimaryGreen,
                    todayDateBorderColor = PrimaryGreen
                )
            )
        }
    }
    
    // Notes Dialog
    if (showNotesDialog) {
        var tempNotes by remember { mutableStateOf(country.notes) }
        val maxLength = UpdateCountryNotesUseCase.MAX_NOTES_LENGTH
        val isOverLimit = tempNotes.length > maxLength

        AlertDialog(
            onDismissRequest = { showNotesDialog = false },
            title = { Text(stringResource(R.string.edit_notes)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = tempNotes,
                        onValueChange = { newValue ->
                            // Allow typing but show error if over limit
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
                                color = if (isOverLimit) MaterialTheme.colorScheme.error else OnSurfaceVariant
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isOverLimit) MaterialTheme.colorScheme.error else PrimaryGreen,
                            errorBorderColor = MaterialTheme.colorScheme.error
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateNotes(tempNotes)
                        showNotesDialog = false
                    },
                    enabled = !isOverLimit
                ) {
                    Text(
                        stringResource(R.string.save),
                        color = if (isOverLimit) OnSurfaceVariant else PrimaryGreen
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotesDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            containerColor = Surface
        )
    }
}

@Composable
fun HeroCard(
    country: Country,
    modifier: Modifier = Modifier
) {
    // Pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    
    Card(
        modifier = modifier.height(240.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(SecondaryContainer, Color(0xFFB8D9C9))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Pulsing glow effect
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = pulseAlpha),
                                Color.Transparent
                            )
                        )
                    )
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = country.flagEmoji,
                    fontSize = 120.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = country.name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnSurface
                )
                Text(
                    text = country.region,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun VisitStatusCard(
    country: Country,
    onEditDate: () -> Unit,
    onMarkAsUnvisited: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(PrimaryContainer, Color(0xFF9FFFD4))
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✓ ${stringResource(R.string.visited_status)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    TextButton(onClick = onMarkAsUnvisited) {
                        Text(stringResource(R.string.mark_as_not_visited), color = PrimaryGreen)
                    }
                }

                if (country.visitedDate != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = PrimaryGreen
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                            val dateString = dateFormat.format(Date(country.visitedDate))
                            Text(
                                text = dateString,
                                fontSize = 16.sp,
                                color = OnSurface
                            )
                        }
                        IconButton(onClick = onEditDate) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(R.string.content_desc_edit_date),
                                tint = PrimaryGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RatingCard(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.your_rating),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..5) {
                    IconButton(
                        onClick = { onRatingChange(i) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = stringResource(R.string.content_desc_rate_stars, i),
                            tint = StarYellow,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotesCard(
    notes: String,
    onEditNotes: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.notes_title),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
                IconButton(onClick = onEditNotes) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.content_desc_edit_notes),
                        tint = PrimaryGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (notes.isNotEmpty()) {
                Text(
                    text = notes,
                    fontSize = 14.sp,
                    color = OnSurface,
                    lineHeight = 20.sp
                )
            } else {
                Text(
                    text = stringResource(R.string.no_notes_placeholder),
                    fontSize = 14.sp,
                    color = OnSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
