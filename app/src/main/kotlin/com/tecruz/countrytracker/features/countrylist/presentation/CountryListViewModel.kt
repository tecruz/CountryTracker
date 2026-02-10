package com.tecruz.countrytracker.features.countrylist.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecruz.countrytracker.features.countrylist.domain.GetAllCountriesUseCase
import com.tecruz.countrytracker.features.countrylist.domain.GetCountryStatisticsUseCase
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val KEY_SEARCH_QUERY = "search_query"
private const val KEY_SELECTED_REGION = "selected_region"
private const val KEY_SHOW_ONLY_VISITED = "show_only_visited"

@OptIn(FlowPreview::class)
@HiltViewModel
class CountryListViewModel @Inject constructor(
    private val getAllCountriesUseCase: GetAllCountriesUseCase,
    private val getCountryStatisticsUseCase: GetCountryStatisticsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Use SavedStateHandle to survive process death
    private val _searchQuery = savedStateHandle.getStateFlow(KEY_SEARCH_QUERY, "")
    private val _selectedRegion = savedStateHandle.getStateFlow(KEY_SELECTED_REGION, "All")
    private val _showOnlyVisited = savedStateHandle.getStateFlow(KEY_SHOW_ONLY_VISITED, false)

    // Manual error flow for clearError support
    private val _manualError = MutableStateFlow<String?>(null)

    // Debounced search query - waits 300ms after user stops typing
    private val debouncedSearchQuery = _searchQuery
        .debounce(300)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "",
        )

    val uiState: StateFlow<CountryListUiState> = combine(
        combine(
            getAllCountriesUseCase()
                .catch {
                    _manualError.value = it.message ?: "Failed to load countries"
                    emit(emptyList())
                },
            getCountryStatisticsUseCase()
                .catch {
                    _manualError.value = it.message ?: "Failed to load statistics"
                    emit(com.tecruz.countrytracker.features.countrylist.domain.CountryStatistics(0, 0, 0))
                },
            _searchQuery,
        ) {
                countries: List<CountryListItem>,
                stats: com.tecruz.countrytracker.features.countrylist.domain.CountryStatistics,
                immediateSearchQuery: String,
            ->
            Triple(countries, stats, immediateSearchQuery)
        },
        combine(
            debouncedSearchQuery,
            _selectedRegion,
            _showOnlyVisited,
        ) { debouncedQuery: String, selectedRegion: String, showOnlyVisited: Boolean ->
            Triple(debouncedQuery, selectedRegion, showOnlyVisited)
        },
        _manualError,
    ) { dataTriple, filterTriple, manualError ->
        val (countries, stats, immediateSearchQuery) = dataTriple
        val (debouncedQuery, selectedRegion, showOnlyVisited) = filterTriple
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
                error = manualError,
            )
        } catch (e: Exception) {
            _manualError.value = e.message ?: "An unexpected error occurred"
            CountryListUiState(
                isLoading = false,
                error = e.message ?: "An unexpected error occurred",
            )
        }
    }.catch { e ->
        _manualError.value = e.message ?: "Failed to load countries"
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
        savedStateHandle[KEY_SEARCH_QUERY] = query
    }

    fun selectRegion(region: String) {
        savedStateHandle[KEY_SELECTED_REGION] = region
    }

    fun toggleShowOnlyVisited() {
        savedStateHandle[KEY_SHOW_ONLY_VISITED] = !_showOnlyVisited.value
    }

    fun clearError() {
        _manualError.value = null
    }
}
