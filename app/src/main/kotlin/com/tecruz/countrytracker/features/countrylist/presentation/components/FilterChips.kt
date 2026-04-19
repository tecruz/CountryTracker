package com.tecruz.countrytracker.features.countrylist.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecruz.countrytracker.R
import com.tecruz.countrytracker.core.designsystem.Outline
import com.tecruz.countrytracker.core.designsystem.PrimaryGreen
import com.tecruz.countrytracker.core.designsystem.PrimaryLight

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

        item {
            FilterChip(
                selected = selectedRegion == "All" || selectedRegion == allRegionLabel,
                onClick = { onRegionSelect("All") },
                label = {
                    Text(
                        allRegionLabel,
                        fontWeight = if (selectedRegion == "All" || selectedRegion == allRegionLabel) {
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
                    borderColor = if (selectedRegion == "All" || selectedRegion == allRegionLabel) {
                        Color.Transparent
                    } else {
                        Outline.copy(alpha = 0.5f)
                    },
                    selectedBorderColor = Color.Transparent,
                ),
            )
        }

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
