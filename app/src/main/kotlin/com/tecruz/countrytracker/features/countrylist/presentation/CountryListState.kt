package com.tecruz.countrytracker.features.countrylist.presentation

import androidx.compose.runtime.Stable
import com.tecruz.countrytracker.core.presentation.UiText
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem

@Stable
data class CountryListState(
    val countries: List<CountryListItem> = emptyList(),
    val visitedCountryCodes: Set<String> = emptySet(),
    val visitedCount: Int = 0,
    val totalCount: Int = 0,
    val percentage: Int = 0,
    val selectedRegion: String = "All",
    val searchQuery: String = "",
    val showOnlyVisited: Boolean = false,
    val isLoading: Boolean = true,
    val allRegions: List<String> = emptyList(),
    val error: UiText? = null,
)
