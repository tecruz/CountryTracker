package com.tecruz.countrytracker.features.countrylist.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecruz.countrytracker.core.presentation.UiText
import com.tecruz.countrytracker.features.countrylist.domain.GetAllCountriesUseCase
import com.tecruz.countrytracker.features.countrylist.domain.GetAllRegionsUseCase
import com.tecruz.countrytracker.features.countrylist.domain.GetCountryStatisticsUseCase
import com.tecruz.countrytracker.features.countrylist.domain.GetVisitedCountryCodesUseCase
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryStatistics
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val KEY_SEARCH_QUERY = "search_query"
private const val KEY_SELECTED_REGION = "selected_region"
private const val KEY_SHOW_ONLY_VISITED = "show_only_visited"

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class CountryListViewModel(
    private val getAllCountriesUseCase: GetAllCountriesUseCase,
    private val getCountryStatisticsUseCase: GetCountryStatisticsUseCase,
    private val getAllRegionsUseCase: GetAllRegionsUseCase,
    private val getVisitedCountryCodesUseCase: GetVisitedCountryCodesUseCase,
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
    private val debouncedSearchQuery = _searchQuery.debounce(300)

    /**
     * Dedicated flow for fetching countries reactively based on filters.
     */
    private val filteredCountriesFlow = combine(
        debouncedSearchQuery,
        _selectedRegion,
        _showOnlyVisited,
    ) { q, r, v -> Triple(q, r, v) }
        .flatMapLatest { (q, r, v) ->
            getAllCountriesUseCase(q, r, v)
                .catch { e ->
                    _manualError.value = UiText.DynamicString(e.message ?: "Failed to load countries")
                    emit(emptyList())
                }
        }

    /**
     * Dedicated flow for current UI filter values (immediate updates).
     */
    private val currentFiltersFlow = combine(
        _searchQuery,
        _selectedRegion,
        _showOnlyVisited,
        _manualError,
    ) { sq, sr, sov, err ->
        Filters(sq, sr, sov, err)
    }

    /**
     * Main UI state, combining domain data and immediate filter state.
     */
    val state: StateFlow<CountryListState> = combine(
        filteredCountriesFlow,
        getCountryStatisticsUseCase().catch { emit(CountryStatistics(0, 0, 0)) },
        getVisitedCountryCodesUseCase().catch { emit(emptySet()) },
        getAllRegionsUseCase().catch { emit(emptyList()) },
        currentFiltersFlow,
    ) { countries, stats, visitedCodes, regions, filters ->
        CountryListState(
            countries = countries,
            visitedCountryCodes = visitedCodes,
            visitedCount = stats.visitedCount,
            totalCount = stats.totalCount,
            percentage = stats.percentage,
            searchQuery = filters.query,
            selectedRegion = filters.region,
            showOnlyVisited = filters.showOnlyVisited,
            error = filters.error,
            isLoading = false,
            allRegions = regions,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CountryListState(isLoading = true),
    )

    private data class Filters(val query: String, val region: String, val showOnlyVisited: Boolean, val error: UiText?)

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
