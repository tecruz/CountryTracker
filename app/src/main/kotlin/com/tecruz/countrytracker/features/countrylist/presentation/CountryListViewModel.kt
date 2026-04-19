package com.tecruz.countrytracker.features.countrylist.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecruz.countrytracker.core.presentation.UiText
import com.tecruz.countrytracker.features.countrylist.domain.GetAllCountriesUseCase
import com.tecruz.countrytracker.features.countrylist.domain.GetCountryStatisticsUseCase
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryStatistics
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val KEY_SEARCH_QUERY = "search_query"
private const val KEY_SELECTED_REGION = "selected_region"
private const val KEY_SHOW_ONLY_VISITED = "show_only_visited"

@OptIn(FlowPreview::class)
class CountryListViewModel(
    private val getAllCountriesUseCase: GetAllCountriesUseCase,
    private val getCountryStatisticsUseCase: GetCountryStatisticsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Use SavedStateHandle to survive process death
    private val _searchQuery = savedStateHandle.getStateFlow(KEY_SEARCH_QUERY, "")
    private val _selectedRegion = savedStateHandle.getStateFlow(KEY_SELECTED_REGION, "All")
    private val _showOnlyVisited = savedStateHandle.getStateFlow(KEY_SHOW_ONLY_VISITED, false)

    // Manual error flow for clearError support
    private val _manualError = MutableStateFlow<UiText?>(null)

    // Event channel for one-time events
    private val _events = Channel<CountryListEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // Debounced search query - waits 300ms after user stops typing
    private val debouncedSearchQuery = _searchQuery
        .debounce(300)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "",
        )

    val state: StateFlow<CountryListState> = combine(
        combine(
            getAllCountriesUseCase()
                .catch { e ->
                    _manualError.value = UiText.DynamicString(e.message ?: "Failed to load countries")
                    emit(emptyList())
                },
            getCountryStatisticsUseCase()
                .catch { e ->
                    _manualError.value = UiText.DynamicString(e.message ?: "Failed to load statistics")
                    emit(CountryStatistics(0, 0, 0))
                },
            _searchQuery,
        ) { countries: List<CountryListItem>, stats: CountryStatistics, immediateSearchQuery: String ->
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

            CountryListState(
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
            _manualError.value = UiText.DynamicString(e.message ?: "An unexpected error occurred")
            CountryListState(
                isLoading = false,
                error = _manualError.value,
            )
        }
    }.catch { e ->
        _manualError.value = UiText.DynamicString(e.message ?: "Failed to load countries")
        emit(
            CountryListState(
                isLoading = false,
                error = _manualError.value,
            ),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CountryListState(),
    )

    fun onAction(action: CountryListAction) {
        when (action) {
            is CountryListAction.OnSearchQueryChange -> updateSearchQuery(action.query)
            is CountryListAction.OnRegionSelect -> selectRegion(action.region)
            is CountryListAction.OnToggleShowOnlyVisited -> toggleShowOnlyVisited()
            is CountryListAction.OnCountryClick -> onCountryClick(action.code)
            is CountryListAction.OnClearError -> clearError()
        }
    }

    private fun updateSearchQuery(query: String) {
        savedStateHandle[KEY_SEARCH_QUERY] = query
    }

    private fun selectRegion(region: String) {
        savedStateHandle[KEY_SELECTED_REGION] = region
    }

    private fun toggleShowOnlyVisited() {
        savedStateHandle[KEY_SHOW_ONLY_VISITED] = !_showOnlyVisited.value
    }

    private fun onCountryClick(countryCode: String) {
        viewModelScope.launch {
            _events.send(CountryListEvent.NavigateToDetail(countryCode))
        }
    }

    private fun clearError() {
        _manualError.value = null
    }
}
