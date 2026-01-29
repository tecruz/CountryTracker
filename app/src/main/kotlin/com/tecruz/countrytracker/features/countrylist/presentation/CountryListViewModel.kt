package com.tecruz.countrytracker.features.countrylist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecruz.countrytracker.features.countrylist.domain.GetAllCountriesUseCase
import com.tecruz.countrytracker.features.countrylist.domain.GetCountryStatisticsUseCase
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class CountryListUiState(
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
    val error: String? = null,
)

@OptIn(FlowPreview::class)
@HiltViewModel
class CountryListViewModel @Inject constructor(
    private val getAllCountriesUseCase: GetAllCountriesUseCase,
    private val getCountryStatisticsUseCase: GetCountryStatisticsUseCase,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedRegion = MutableStateFlow("All")
    private val _showOnlyVisited = MutableStateFlow(false)

    // Debounced search query - waits 300ms after user stops typing
    private val debouncedSearchQuery = _searchQuery
        .debounce(300)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "",
        )

    val uiState: StateFlow<CountryListUiState> = combine(
        getAllCountriesUseCase()
            .catch { emit(emptyList()) },
        getCountryStatisticsUseCase()
            .catch { emit(com.tecruz.countrytracker.features.countrylist.domain.CountryStatistics(0, 0, 0)) },
        _searchQuery,
        debouncedSearchQuery,
        _selectedRegion,
        _showOnlyVisited,
    ) { flows ->
        @Suppress("UNCHECKED_CAST")
        val countries = flows[0] as List<CountryListItem>
        val stats = flows[1] as com.tecruz.countrytracker.features.countrylist.domain.CountryStatistics
        val immediateSearchQuery = flows[2] as String
        val debouncedQuery = flows[3] as String
        val selectedRegion = flows[4] as String
        val showOnlyVisited = flows[5] as Boolean
        try {
            val filteredCountries = countries
                .filter { country ->
                    val regionMatch = selectedRegion == "All" || country.region == selectedRegion
                    val visitedMatch = !showOnlyVisited || country.visited
                    val searchMatch = debouncedQuery.isEmpty() ||
                        country.name.contains(debouncedQuery, ignoreCase = true)
                    regionMatch && visitedMatch && searchMatch
                }

            val regions = countries.map { it.region }.distinct().sorted()

            // Compute visited country codes from ALL countries (not filtered)
            val visitedCodes = countries.filter { it.visited }.map { it.code }.toSet()

            CountryListUiState(
                countries = filteredCountries,
                visitedCountryCodes = visitedCodes,
                visitedCount = stats.visitedCount,
                totalCount = stats.totalCount,
                percentage = stats.percentage,
                selectedRegion = selectedRegion,
                searchQuery = immediateSearchQuery,
                showOnlyVisited = showOnlyVisited,
                isLoading = false,
                allRegions = regions,
                error = null,
            )
        } catch (e: Exception) {
            CountryListUiState(
                isLoading = false,
                error = e.message ?: "An unexpected error occurred",
            )
        }
    }.catch { e ->
        emit(
            CountryListUiState(
                isLoading = false,
                error = e.message ?: "Failed to load countries",
            ),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CountryListUiState(),
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectRegion(region: String) {
        _selectedRegion.value = region
    }

    fun toggleShowOnlyVisited() {
        _showOnlyVisited.value = !_showOnlyVisited.value
    }

    fun clearError() {
        // Error is automatically cleared on next successful data load
    }
}
