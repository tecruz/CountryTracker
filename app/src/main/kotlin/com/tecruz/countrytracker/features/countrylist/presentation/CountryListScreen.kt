@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.tecruz.countrytracker.features.countrylist.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecruz.countrytracker.LocalWindowSizeClass
import com.tecruz.countrytracker.R
import com.tecruz.countrytracker.core.designsystem.Background
import com.tecruz.countrytracker.core.designsystem.OnSurface
import com.tecruz.countrytracker.core.designsystem.OnSurfaceVariant
import com.tecruz.countrytracker.core.designsystem.PrimaryGreen
import com.tecruz.countrytracker.core.designsystem.TopBarGradientEnd
import com.tecruz.countrytracker.core.designsystem.TopBarGradientStart
import com.tecruz.countrytracker.core.designsystem.preview.DevicePreviews
import com.tecruz.countrytracker.core.designsystem.preview.ExcludeFromGeneratedCoverageReport
import com.tecruz.countrytracker.core.designsystem.preview.PreviewCountryListItems
import com.tecruz.countrytracker.core.designsystem.preview.PreviewWrapper
import com.tecruz.countrytracker.core.presentation.asString
import com.tecruz.countrytracker.core.util.contentPadding
import com.tecruz.countrytracker.core.util.gridColumns
import com.tecruz.countrytracker.core.util.horizontalPadding
import com.tecruz.countrytracker.core.util.itemSpacing
import com.tecruz.countrytracker.features.countrylist.presentation.components.CountryListItem
import com.tecruz.countrytracker.features.countrylist.presentation.components.FilterChips
import com.tecruz.countrytracker.features.countrylist.presentation.components.SearchBar
import com.tecruz.countrytracker.features.countrylist.presentation.components.StatsCard
import com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.WorldMapCanvas
import com.tecruz.countrytracker.features.countrylist.presentation.model.TabItem
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.lazy.grid.itemsIndexed as gridItemsIndexed

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CountryListRoot(onCountryClick: (String) -> Unit, modifier: Modifier = Modifier) {
    val viewModel: CountryListViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val errorDismissText = stringResource(R.string.error_dismiss)

    // Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CountryListEvent.NavigateToDetail -> onCountryClick(event.countryCode)
                is CountryListEvent.ShowSnackbar -> {
                    val message = event.message.asString()
                    snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = errorDismissText,
                    )
                }
            }
        }
    }

    // Show error snackbar when error occurs
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error.asString(),
                actionLabel = errorDismissText,
            )
        }
    }

    CountryListScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CountryListScreen(
    state: CountryListState,
    onAction: (CountryListAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
) {
    val windowSizeClass = LocalWindowSizeClass.current
    val hPadding = windowSizeClass.horizontalPadding()
    val cPadding = windowSizeClass.contentPadding()
    val iSpacing = windowSizeClass.itemSpacing()
    val scope = rememberCoroutineScope()

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
            if (snackbarHostState != null) {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
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
                                    fontWeight = if (pagerState.currentPage == index) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Medium
                                    },
                                    fontSize = 15.sp,
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = if (pagerState.currentPage == index) {
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
                        state = state,
                        onAction = onAction,
                        contentPadding = cPadding,
                        itemSpacing = iSpacing,
                        gridColumns = windowSizeClass.gridColumns(),
                    )
                    1 -> MapTab(
                        state = state,
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
    state: CountryListState,
    modifier: Modifier = Modifier,
    horizontalPadding: androidx.compose.ui.unit.Dp = 16.dp,
) {
    // Use visitedCountryCodes from state (computed from ALL countries, not filtered)
    val visitedCountryCodes = state.visitedCountryCodes
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
            visitedCount = state.visitedCount,
            totalCount = state.totalCount,
            percentage = state.percentage,
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
    state: CountryListState,
    onAction: (CountryListAction) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: androidx.compose.ui.unit.Dp = 20.dp,
    itemSpacing: androidx.compose.ui.unit.Dp = 12.dp,
    gridColumns: Int = 1,
) {
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()

    // Scroll to top when filters change
    LaunchedEffect(state.searchQuery, state.selectedRegion, state.showOnlyVisited) {
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
            query = state.searchQuery,
            onQueryChange = { onAction(CountryListAction.OnSearchQueryChange(it)) },
            modifier = Modifier.padding(horizontal = contentPadding),
        )

        Spacer(modifier = Modifier.height(itemSpacing))

        // Filter Chips
        FilterChips(
            allRegions = state.allRegions,
            selectedRegion = state.selectedRegion,
            showOnlyVisited = state.showOnlyVisited,
            onToggleVisited = { onAction(CountryListAction.OnToggleShowOnlyVisited) },
            onRegionSelect = { onAction(CountryListAction.OnRegionSelect(it)) },
            modifier = Modifier.padding(horizontal = contentPadding),
        )

        Spacer(modifier = Modifier.height(itemSpacing))

        // Country List
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                LoadingIndicator(color = PrimaryGreen)
            }
        } else if (state.countries.isEmpty()) {
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
                        items = state.countries,
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
                                onClick = { onAction(CountryListAction.OnCountryClick(country.code)) },
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
                        items = state.countries,
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
                                onClick = { onAction(CountryListAction.OnCountryClick(country.code)) },
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
            allRegions = listOf("Africa", "Americas", "Asia", "Europe", "Oceania"),
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

// endregion
