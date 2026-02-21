@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.tecruz.countrytracker.features.countrylist.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecruz.countrytracker.LocalWindowSizeClass
import com.tecruz.countrytracker.R
import com.tecruz.countrytracker.core.designsystem.Background
import com.tecruz.countrytracker.core.designsystem.CountryItemNeutralEnd
import com.tecruz.countrytracker.core.designsystem.CountryItemVisitedEnd
import com.tecruz.countrytracker.core.designsystem.CountryItemVisitedStart
import com.tecruz.countrytracker.core.designsystem.OnSurface
import com.tecruz.countrytracker.core.designsystem.OnSurfaceVariant
import com.tecruz.countrytracker.core.designsystem.Outline
import com.tecruz.countrytracker.core.designsystem.PrimaryGreen
import com.tecruz.countrytracker.core.designsystem.PrimaryLight
import com.tecruz.countrytracker.core.designsystem.StatsGradientEnd
import com.tecruz.countrytracker.core.designsystem.StatsGradientMid
import com.tecruz.countrytracker.core.designsystem.StatsGradientStart
import com.tecruz.countrytracker.core.designsystem.StatsIconComplete
import com.tecruz.countrytracker.core.designsystem.StatsIconTotal
import com.tecruz.countrytracker.core.designsystem.StatsIconVisited
import com.tecruz.countrytracker.core.designsystem.StatsLabelColor
import com.tecruz.countrytracker.core.designsystem.StatsValueColor
import com.tecruz.countrytracker.core.designsystem.TopBarGradientEnd
import com.tecruz.countrytracker.core.designsystem.TopBarGradientStart
import com.tecruz.countrytracker.core.designsystem.preview.DevicePreviews
import com.tecruz.countrytracker.core.designsystem.preview.ExcludeFromGeneratedCoverageReport
import com.tecruz.countrytracker.core.designsystem.preview.PreviewCountryListItems
import com.tecruz.countrytracker.core.designsystem.preview.PreviewWrapper
import com.tecruz.countrytracker.core.designsystem.preview.previewRegions
import com.tecruz.countrytracker.core.util.contentPadding
import com.tecruz.countrytracker.core.util.gridColumns
import com.tecruz.countrytracker.core.util.horizontalPadding
import com.tecruz.countrytracker.core.util.itemSpacing
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem
import com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.WorldMapCanvas
import com.tecruz.countrytracker.features.countrylist.presentation.model.TabItem
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.grid.itemsIndexed as gridItemsIndexed

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CountryListScreen(
    onCountryClick: (String) -> Unit,
    viewModel: CountryListViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val errorDismissText = stringResource(R.string.error_dismiss)
    val scope = rememberCoroutineScope()
    val windowSizeClass = LocalWindowSizeClass.current
    val hPadding = windowSizeClass.horizontalPadding()
    val cPadding = windowSizeClass.contentPadding()
    val iSpacing = windowSizeClass.itemSpacing()

    // Tab state
    val tabItems = listOf(
        TabItem(
            title = stringResource(R.string.tab_countries),
            selectedIcon = Icons.AutoMirrored.Filled.List,
            unselectedIcon = Icons.AutoMirrored.Outlined.List,
        ),
        TabItem(
            title = stringResource(R.string.tab_map),
            selectedIcon = Icons.Filled.Map,
            unselectedIcon = Icons.Outlined.Map,
        ),
    )
    // Save the current tab index to survive process death
    val savedPageIndex = rememberSaveable { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(
        initialPage = savedPageIndex.intValue,
        pageCount = { tabItems.size },
    )
    // Update saved index when page changes
    LaunchedEffect(pagerState.currentPage) {
        savedPageIndex.intValue = pagerState.currentPage
    }

    // Show error snackbar when error occurs
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                actionLabel = errorDismissText,
            )
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                TopBarGradientStart,
                                PrimaryGreen,
                                TopBarGradientEnd,
                            ),
                        ),
                    )
                    .windowInsetsPadding(WindowInsets.statusBars),
            ) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp),
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                stringResource(R.string.title_country_tracker),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp,
                                letterSpacing = 0.5.sp,
                            )
                        }
                    },
                    windowInsets = WindowInsets(0, 0, 0, 0),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                    ),
                )
            }
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
                .background(Background),
        ) {
            // Tab Row with enhanced styling using M3 Expressive PrimaryTabRow
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                PrimaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = Color.White,
                    contentColor = PrimaryGreen,
                ) {
                    tabItems.forEachIndexed { index, tabItem ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = tabItem.title,
                                    fontWeight = if (pagerState.currentPage ==
                                        index
                                    ) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Medium
                                    },
                                    fontSize = 15.sp,
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = if (pagerState.currentPage ==
                                        index
                                    ) {
                                        tabItem.selectedIcon
                                    } else {
                                        tabItem.unselectedIcon
                                    },
                                    contentDescription = tabItem.title,
                                    modifier = Modifier.size(24.dp),
                                )
                            },
                            selectedContentColor = PrimaryGreen,
                            unselectedContentColor = OnSurfaceVariant,
                        )
                    }
                }
            }

            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                when (page) {
                    0 -> ListTab(
                        uiState = uiState,
                        onCountryClick = onCountryClick,
                        onSearchQueryChange = viewModel::updateSearchQuery,
                        onToggleVisited = viewModel::toggleShowOnlyVisited,
                        onRegionSelect = viewModel::selectRegion,
                        contentPadding = cPadding,
                        itemSpacing = iSpacing,
                        gridColumns = windowSizeClass.gridColumns(),
                    )
                    1 -> MapTab(
                        uiState = uiState,
                        horizontalPadding = hPadding,
                    )
                }
            }
        }
    }
}

/**
 * Map Tab - Shows world map and statistics
 * Uses scrollable layout to handle landscape orientation properly
 */
@Composable
private fun MapTab(
    uiState: CountryListUiState,
    modifier: Modifier = Modifier,
    horizontalPadding: androidx.compose.ui.unit.Dp = 16.dp,
) {
    // Use visitedCountryCodes from uiState (computed from ALL countries, not filtered)
    val visitedCountryCodes = uiState.visitedCountryCodes
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(scrollState)
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // World Map in a card for elevation
        // Uses aspectRatio to maintain proper proportions in both orientations
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .padding(horizontal = horizontalPadding),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            WorldMapCanvas(
                visitedCountryCodes = visitedCountryCodes,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Statistics Card with enhanced design
        StatsCard(
            visitedCount = uiState.visitedCount,
            totalCount = uiState.totalCount,
            percentage = uiState.percentage,
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 12.dp),
        )

        // Hint text with better styling (only show if not empty)
        val hintText = stringResource(R.string.map_hint)
        if (hintText.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.6f),
                ),
            ) {
                Text(
                    text = hintText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding, vertical = 12.dp),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * List Tab - Shows search, filters, and country list
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListTab(
    uiState: CountryListUiState,
    onCountryClick: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleVisited: () -> Unit,
    onRegionSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: androidx.compose.ui.unit.Dp = 20.dp,
    itemSpacing: androidx.compose.ui.unit.Dp = 12.dp,
    gridColumns: Int = 1,
) {
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()

    // Scroll to top when filters change
    LaunchedEffect(uiState.searchQuery, uiState.selectedRegion, uiState.showOnlyVisited) {
        listState.animateScrollToItem(0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        SearchBar(
            query = uiState.searchQuery,
            onQueryChange = onSearchQueryChange,
            modifier = Modifier.padding(horizontal = contentPadding),
        )

        Spacer(modifier = Modifier.height(itemSpacing))

        // Filter Chips
        FilterChips(
            allRegions = uiState.allRegions,
            selectedRegion = uiState.selectedRegion,
            showOnlyVisited = uiState.showOnlyVisited,
            onToggleVisited = onToggleVisited,
            onRegionSelect = onRegionSelect,
            modifier = Modifier.padding(horizontal = contentPadding),
        )

        Spacer(modifier = Modifier.height(itemSpacing))

        // Country List
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                LoadingIndicator(color = PrimaryGreen)
            }
        } else if (uiState.countries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.no_countries_found),
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnSurfaceVariant,
                )
            }
        } else {
            val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
            val listContentPadding = PaddingValues(
                start = contentPadding,
                end = contentPadding,
                top = 8.dp,
                bottom = 8.dp + navBarPadding.calculateBottomPadding(),
            )

            if (gridColumns > 1) {
                // T035: Multi-column grid layout for medium/expanded screens
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridColumns),
                    state = gridState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = listContentPadding,
                    verticalArrangement = Arrangement.spacedBy(itemSpacing),
                    horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                ) {
                    gridItemsIndexed(
                        items = uiState.countries,
                        key = { _, country -> country.code },
                    ) { index, country ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = tween(
                                    durationMillis = 500,
                                    delayMillis = index * 50,
                                ),
                            ) + fadeIn(
                                animationSpec = tween(
                                    durationMillis = 500,
                                    delayMillis = index * 50,
                                ),
                            ),
                        ) {
                            CountryListItem(
                                country = country,
                                onClick = { onCountryClick(country.code) },
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }
                }
            } else {
                // Compact: single-column LazyColumn
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = listContentPadding,
                ) {
                    itemsIndexed(
                        items = uiState.countries,
                        key = { _, country -> country.code },
                    ) { index, country ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = tween(
                                    durationMillis = 500,
                                    delayMillis = index * 50,
                                ),
                            ) + fadeIn(
                                animationSpec = tween(
                                    durationMillis = 500,
                                    delayMillis = index * 50,
                                ),
                            ),
                        ) {
                            CountryListItem(
                                country = country,
                                onClick = { onCountryClick(country.code) },
                                modifier = Modifier.animateItem(),
                            )
                        }
                        Spacer(modifier = Modifier.height(itemSpacing))
                    }
                }
            }
        }
    }
}

@Composable
fun StatsCard(visitedCount: Int, totalCount: Int, percentage: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 8.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            StatsGradientStart,
                            StatsGradientMid,
                            StatsGradientEnd,
                        ),
                    ),
                )
                .padding(28.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatItem(
                    icon = Icons.Default.Flag,
                    value = visitedCount.toString(),
                    label = stringResource(R.string.stats_visited),
                    iconTint = StatsIconVisited,
                )

                HorizontalDivider(
                    modifier = Modifier
                        .height(60.dp)
                        .width(2.dp),
                    color = PrimaryGreen.copy(alpha = 0.2f),
                )

                StatItem(
                    icon = Icons.Default.Public,
                    value = totalCount.toString(),
                    label = stringResource(R.string.stats_total),
                    iconTint = StatsIconTotal,
                )

                HorizontalDivider(
                    modifier = Modifier
                        .height(60.dp)
                        .width(2.dp),
                    color = PrimaryGreen.copy(alpha = 0.2f),
                )

                StatItem(
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    value = "$percentage%",
                    label = stringResource(R.string.stats_complete),
                    iconTint = StatsIconComplete,
                )
            }
        }
    }
}

@Composable
fun StatItem(icon: ImageVector, value: String, label: String, iconTint: Color, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(28.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold,
            color = StatsValueColor,
            lineHeight = 40.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = StatsLabelColor,
            letterSpacing = 0.8.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    stringResource(R.string.search_placeholder),
                    color = OnSurfaceVariant.copy(alpha = 0.6f),
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.content_desc_search),
                    tint = PrimaryGreen,
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = stringResource(R.string.content_desc_clear_search),
                            tint = OnSurfaceVariant,
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen.copy(alpha = 0.8f),
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
            ),
        )
    }
}

@Composable
fun FilterChips(
    allRegions: List<String>,
    selectedRegion: String,
    showOnlyVisited: Boolean,
    onToggleVisited: () -> Unit,
    onRegionSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val allRegionLabel = stringResource(R.string.region_all)

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Visited only chip
        item {
            FilterChip(
                selected = showOnlyVisited,
                onClick = onToggleVisited,
                label = {
                    Text(
                        stringResource(R.string.filter_visited_only),
                        fontWeight = if (showOnlyVisited) FontWeight.Bold else FontWeight.Medium,
                    )
                },
                leadingIcon = if (showOnlyVisited) {
                    {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                        )
                    }
                } else {
                    null
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryGreen,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = showOnlyVisited,
                    borderColor = if (showOnlyVisited) Color.Transparent else Outline.copy(alpha = 0.5f),
                    selectedBorderColor = Color.Transparent,
                ),
            )
        }

        // "All" region chip
        item {
            FilterChip(
                selected = selectedRegion == "All" || selectedRegion == allRegionLabel,
                onClick = { onRegionSelect("All") },
                label = {
                    Text(
                        allRegionLabel,
                        fontWeight = if (selectedRegion == "All" ||
                            selectedRegion == allRegionLabel
                        ) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Medium
                        },
                    )
                },
                leadingIcon = if (selectedRegion == "All" || selectedRegion == allRegionLabel) {
                    {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                        )
                    }
                } else {
                    null
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryLight,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedRegion == "All" || selectedRegion == allRegionLabel,
                    borderColor = if (selectedRegion == "All" ||
                        selectedRegion == allRegionLabel
                    ) {
                        Color.Transparent
                    } else {
                        Outline.copy(alpha = 0.5f)
                    },
                    selectedBorderColor = Color.Transparent,
                ),
            )
        }

        // Region chips
        items(allRegions) { region ->
            FilterChip(
                selected = selectedRegion == region,
                onClick = { onRegionSelect(region) },
                label = {
                    Text(
                        region,
                        fontWeight = if (selectedRegion == region) FontWeight.Bold else FontWeight.Medium,
                    )
                },
                leadingIcon = if (selectedRegion == region) {
                    {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                        )
                    }
                } else {
                    null
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryLight,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedRegion == region,
                    borderColor = if (selectedRegion == region) Color.Transparent else Outline.copy(alpha = 0.5f),
                    selectedBorderColor = Color.Transparent,
                ),
            )
        }
    }
}

@Composable
fun CountryListItem(country: CountryListItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val backgroundColor = if (country.visited) {
        Brush.linearGradient(
            colors = listOf(
                CountryItemVisitedStart,
                CountryItemVisitedEnd,
            ),
        )
    } else {
        Brush.linearGradient(colors = listOf(Color.White, CountryItemNeutralEnd))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp) // T018: Ensure minimum 48dp touch target
            .testTag("country_item_${country.name}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (country.visited) 4.dp else 2.dp,
            pressedElevation = 6.dp,
        ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = backgroundColor)
                .padding(18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Flag with background
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = if (country.visited) {
                                PrimaryGreen.copy(alpha = 0.1f)
                            } else {
                                Color.Gray.copy(alpha = 0.05f)
                            },
                            shape = RoundedCornerShape(14.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = country.flagEmoji,
                        fontSize = 36.sp,
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Country Info - T019: prevent text truncation on compact screens
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = country.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = country.region,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = OnSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                // Visited indicator
                if (country.visited) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Visited",
                        tint = PrimaryGreen,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        }
    }
}

// region Previews

@ExcludeFromGeneratedCoverageReport
@DevicePreviews
@Composable
private fun StatsCardPreview() {
    PreviewWrapper {
        StatsCard(
            visitedCount = 42,
            totalCount = 195,
            percentage = 22,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@ExcludeFromGeneratedCoverageReport
@DevicePreviews
@Composable
private fun StatItemPreview() {
    PreviewWrapper {
        StatItem(
            icon = Icons.Default.Flag,
            value = "42",
            label = "VISITED",
            iconTint = StatsIconVisited,
        )
    }
}

@ExcludeFromGeneratedCoverageReport
@DevicePreviews
@Composable
private fun SearchBarPreview() {
    PreviewWrapper {
        Column(modifier = Modifier.padding(16.dp)) {
            SearchBar(query = "", onQueryChange = {})
            Spacer(modifier = Modifier.height(8.dp))
            SearchBar(query = "Japan", onQueryChange = {})
        }
    }
}

@ExcludeFromGeneratedCoverageReport
@DevicePreviews
@Composable
private fun FilterChipsPreview() {
    PreviewWrapper {
        FilterChips(
            allRegions = previewRegions,
            selectedRegion = "Europe",
            showOnlyVisited = false,
            onToggleVisited = {},
            onRegionSelect = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@ExcludeFromGeneratedCoverageReport
@DevicePreviews
@Composable
private fun CountryListItemVisitedPreview() {
    PreviewWrapper {
        CountryListItem(
            country = PreviewCountryListItems.visited,
            onClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@ExcludeFromGeneratedCoverageReport
@DevicePreviews
@Composable
private fun CountryListItemUnvisitedPreview() {
    PreviewWrapper {
        CountryListItem(
            country = PreviewCountryListItems.unvisited,
            onClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@ExcludeFromGeneratedCoverageReport
@DevicePreviews
@Composable
private fun CountryListItemLongNamePreview() {
    PreviewWrapper {
        CountryListItem(
            country = PreviewCountryListItems.longName,
            onClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

// endregion
