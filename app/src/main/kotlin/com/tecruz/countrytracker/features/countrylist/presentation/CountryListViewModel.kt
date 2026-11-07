package com.tecruz.countrytracker.features.countrylist.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecruz.countrytracker.R
import com.tecruz.countrytracker.core.domain.Result
import com.tecruz.countrytracker.core.presentation.UiText
import com.tecruz.countrytracker.features.countrylist.domain.GetAllCountriesUseCase
import com.tecruz.countrytracker.features.countrylist.domain.GetAllRegionsUseCase
import com.tecruz.countrytracker.features.countrylist.domain.GetCountryStatisticsUseCase
import com.tecruz.countrytracker.features.countrylist.domain.GetVisitedCountryCodesUseCase
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryStatistics
import com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.WorldMapFailure
import com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.WorldMapGeometryLoader
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val KEY_SEARCH_QUERY = "search_query"
private const val KEY_SELECTED_REGION = "selected_region"
private const val KEY_SHOW_ONLY_VISITED = "show_only_visited"
private const val SEARCH_DEBOUNCE_MS = 300L
private const val SUBSCRIPTION_TIMEOUT_MS = 5_000L

private val countriesError = UiText.StringResource(R.string.error_load_countries)
private val statisticsError = UiText.StringResource(R.string.error_load_statistics)
private val regionsError = UiText.StringResource(R.string.error_load_regions)
private val visitedCodesError = UiText.StringResource(R.string.error_load_visited)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class CountryListViewModel(
    private val getAllCountriesUseCase: GetAllCountriesUseCase,
    private val getCountryStatisticsUseCase: GetCountryStatisticsUseCase,
    private val getAllRegionsUseCase: GetAllRegionsUseCase,
    private val getVisitedCountryCodesUseCase: GetVisitedCountryCodesUseCase,
    private val worldMapGeometryLoader: WorldMapGeometryLoader,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val searchQuery = savedStateHandle.getStateFlow(KEY_SEARCH_QUERY, "")
    private val selectedRegion = savedStateHandle.getStateFlow(KEY_SELECTED_REGION, "All")
    private val showOnlyVisited = savedStateHandle.getStateFlow(KEY_SHOW_ONLY_VISITED, false)

    private val retryGeneration = MutableStateFlow(0)
    private val isErrorDismissed = MutableStateFlow(false)

    private val eventsChannel = Channel<CountryListEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    private val debouncedSearchQuery = searchQuery.debounce(SEARCH_DEBOUNCE_MS)

    private val countriesFlow = combine(
        debouncedSearchQuery,
        selectedRegion,
        showOnlyVisited,
        retryGeneration,
    ) { query, region, visitedOnly, _ ->
        CountryQuery(query, region, visitedOnly)
    }.flatMapLatest { query ->
        getAllCountriesUseCase(query.text, query.region, query.visitedOnly).asSnapshot(countriesError)
    }

    private val statisticsFlow = retryGeneration.flatMapLatest {
        getCountryStatisticsUseCase().asSnapshot(statisticsError)
    }

    private val visitedCodesFlow = retryGeneration.flatMapLatest {
        getVisitedCountryCodesUseCase().asSnapshot(visitedCodesError)
    }

    private val regionsFlow = retryGeneration.flatMapLatest {
        getAllRegionsUseCase().asSnapshot(regionsError)
    }

    private val filtersFlow = combine(
        searchQuery,
        selectedRegion,
        showOnlyVisited,
        isErrorDismissed,
    ) { query, region, visitedOnly, dismissed ->
        FilterSnapshot(query, region, visitedOnly, dismissed)
    }

    /**
     * List, statistics, visited codes, and regions.
     * Failures stay on [DataReadStatus.Unavailable] with a [UiText] message.
     * They are not replaced with an empty list or zero statistics.
     * Collection stops with the UI via [SharingStarted.WhileSubscribed], and
     * [flatMapLatest] cancels the previous read when filters or retry change.
     */
    val state: StateFlow<CountryListState> = combine(
        countriesFlow,
        statisticsFlow,
        visitedCodesFlow,
        regionsFlow,
        filtersFlow,
    ) { countries, statistics, visitedCodes, regions, filters ->
        toCountryListState(countries, statistics, visitedCodes, regions, filters)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS),
        initialValue = CountryListState(isLoading = true),
    )

    /**
     * Parsed map geometry. The first emission is [DataReadStatus.Loading]; parsing
     * runs in [WorldMapGeometryLoader] on the injected dispatcher, never in composition.
     */
    val mapState: StateFlow<WorldMapState> = retryGeneration.flatMapLatest {
        flow {
            emit(WorldMapState(status = DataReadStatus.Loading))
            emit(readMap())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS),
        initialValue = WorldMapState(),
    )

    fun onAction(action: CountryListAction) {
        when (action) {
            is CountryListAction.OnSearchQueryChange -> updateSearchQuery(action.query)
            is CountryListAction.OnRegionSelect -> selectRegion(action.region)
            is CountryListAction.OnToggleShowOnlyVisited -> toggleShowOnlyVisited()
            is CountryListAction.OnCountryClick -> onCountryClick(action.code)
            is CountryListAction.OnClearError -> clearError()
            is CountryListAction.OnRetry -> retry()
        }
    }

    private fun updateSearchQuery(query: String) {
        savedStateHandle[KEY_SEARCH_QUERY] = query
    }

    private fun selectRegion(region: String) {
        savedStateHandle[KEY_SELECTED_REGION] = region
    }

    private fun toggleShowOnlyVisited() {
        savedStateHandle[KEY_SHOW_ONLY_VISITED] = !showOnlyVisited.value
    }

    private fun onCountryClick(countryCode: String) {
        viewModelScope.launch {
            eventsChannel.send(CountryListEvent.NavigateToDetail(countryCode))
        }
    }

    private fun clearError() {
        isErrorDismissed.value = true
    }

    private fun retry() {
        isErrorDismissed.value = false
        retryGeneration.value = retryGeneration.value + 1
    }

    private suspend fun readMap(): WorldMapState = when (val result = worldMapGeometryLoader.load()) {
        is Result.Success -> WorldMapState(
            status = DataReadStatus.Ready,
            paths = result.data,
        )
        is Result.Failure -> WorldMapState(
            status = DataReadStatus.Unavailable,
            error = result.error.toUiText(),
        )
    }

    private fun toCountryListState(
        countries: Snapshot<List<CountryListItem>>,
        statistics: Snapshot<CountryStatistics>,
        visitedCodes: Snapshot<Set<String>>,
        regions: Snapshot<List<String>>,
        filters: FilterSnapshot,
    ): CountryListState {
        val countryValues = (countries as? Snapshot.Ready)?.value.orEmpty()
        val statisticsValues = (statistics as? Snapshot.Ready)?.value
        val visitedValues = (visitedCodes as? Snapshot.Ready)?.value.orEmpty()
        val regionValues = (regions as? Snapshot.Ready)?.value.orEmpty()
        return CountryListState(
            countries = countryValues,
            countriesStatus = countries.toStatus(),
            visitedCountryCodes = visitedValues,
            visitedCodesStatus = visitedCodes.toStatus(),
            visitedCount = statisticsValues?.visitedCount ?: 0,
            totalCount = statisticsValues?.totalCount ?: 0,
            percentage = statisticsValues?.percentage ?: 0,
            statisticsStatus = statistics.toStatus(),
            searchQuery = filters.query,
            selectedRegion = filters.region,
            showOnlyVisited = filters.visitedOnly,
            error = primaryError(countries, statistics, visitedCodes, regions, filters.isErrorDismissed),
            isLoading = false,
            allRegions = regionValues,
            regionsStatus = regions.toStatus(),
        )
    }

    private fun primaryError(
        countries: Snapshot<List<CountryListItem>>,
        statistics: Snapshot<CountryStatistics>,
        visitedCodes: Snapshot<Set<String>>,
        regions: Snapshot<List<String>>,
        isDismissed: Boolean,
    ): UiText? {
        if (isDismissed) {
            return null
        }
        return countries.messageOrNull()
            ?: statistics.messageOrNull()
            ?: visitedCodes.messageOrNull()
            ?: regions.messageOrNull()
    }

    private data class CountryQuery(val text: String, val region: String, val visitedOnly: Boolean)

    private data class FilterSnapshot(
        val query: String,
        val region: String,
        val visitedOnly: Boolean,
        val isErrorDismissed: Boolean,
    )

    private sealed interface Snapshot<out T> {
        data class Ready<T>(val value: T) : Snapshot<T>

        data class Unavailable(val message: UiText) : Snapshot<Nothing>
    }

    private fun Snapshot<*>.toStatus(): DataReadStatus = when (this) {
        is Snapshot.Ready -> DataReadStatus.Ready
        is Snapshot.Unavailable -> DataReadStatus.Unavailable
    }

    private fun Snapshot<*>.messageOrNull(): UiText? = (this as? Snapshot.Unavailable)?.message

    private fun <T> Flow<T>.asSnapshot(message: UiText): Flow<Snapshot<T>> = map<T, Snapshot<T>> { Snapshot.Ready(it) }
        .catch { emit(Snapshot.Unavailable(message)) }
}

private fun WorldMapFailure.toUiText(): UiText = when (this) {
    WorldMapFailure.AssetReadFailed -> UiText.StringResource(R.string.error_load_map)
    WorldMapFailure.PathParseFailed -> UiText.StringResource(R.string.error_parse_map)
}
