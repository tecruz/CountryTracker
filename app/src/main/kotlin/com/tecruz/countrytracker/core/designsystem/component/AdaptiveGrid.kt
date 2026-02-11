package com.tecruz.countrytracker.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.tecruz.countrytracker.core.util.gridColumns
import com.tecruz.countrytracker.core.util.itemSpacing

/**
 * AdaptiveGrid displays items in a single column on compact screens,
 * and in a multi-column grid on medium/expanded screens.
 *
 * @param items The list of items to display
 * @param windowSizeClass The current window size class
 * @param modifier Modifier for the grid
 * @param contentPadding Padding around the content
 * @param listState LazyListState for single-column mode
 * @param gridState LazyGridState for multi-column mode
 * @param key Key factory for stable item identity
 * @param itemContent Composable content for each item
 */
@Composable
fun <T> AdaptiveGrid(
    items: List<T>,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    listState: LazyListState = rememberLazyListState(),
    gridState: LazyGridState = rememberLazyGridState(),
    key: ((T) -> Any)? = null,
    itemContent: @Composable (T) -> Unit,
) {
    val columns = windowSizeClass.gridColumns()
    val spacing = windowSizeClass.itemSpacing()

    if (columns <= 1) {
        // Compact: single-column LazyColumn
        LazyColumn(
            state = listState,
            modifier = modifier,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(spacing),
        ) {
            items(
                items = items,
                key = key?.let { keyFn -> { item -> keyFn(item) } },
            ) { item ->
                itemContent(item)
            }
        }
    } else {
        // Medium/Expanded: multi-column grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            state = gridState,
            modifier = modifier,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(spacing),
            horizontalArrangement = Arrangement.spacedBy(spacing),
        ) {
            items(
                items = items,
                key = key?.let { keyFn -> { item -> keyFn(item) } },
            ) { item ->
                itemContent(item)
            }
        }
    }
}
