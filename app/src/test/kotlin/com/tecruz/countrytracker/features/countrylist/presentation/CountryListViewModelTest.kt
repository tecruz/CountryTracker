package com.tecruz.countrytracker.features.countrylist.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.tecruz.countrytracker.R
import com.tecruz.countrytracker.core.domain.Result
import com.tecruz.countrytracker.core.presentation.UiText
import com.tecruz.countrytracker.features.countrylist.domain.GetAllCountriesUseCase
import com.tecruz.countrytracker.features.countrylist.domain.GetAllRegionsUseCase
import com.tecruz.countrytracker.features.countrylist.domain.GetCountryStatisticsUseCase
import com.tecruz.countrytracker.features.countrylist.domain.GetVisitedCountryCodesUseCase
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryStatistics
import com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.WorldMapGeometryLoader
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountryListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getAllCountriesUseCase: GetAllCountriesUseCase
    private lateinit var getCountryStatisticsUseCase: GetCountryStatisticsUseCase
    private lateinit var getAllRegionsUseCase: GetAllRegionsUseCase
    private lateinit var getVisitedCountryCodesUseCase: GetVisitedCountryCodesUseCase
    private lateinit var worldMapGeometryLoader: WorldMapGeometryLoader
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: CountryListViewModel

    private val testCountries = listOf(
        CountryListItem("US", "United States", "North America", false, "\uD83C\uDDFA\uD83C\uDDF8"),
        CountryListItem("FR", "France", "Europe", true, "\uD83C\uDDEB\uD83C\uDDF7"),
        CountryListItem("JP", "Japan", "Asia", false, "\uD83C\uDDEF\uD83C\uDDF5"),
        CountryListItem("BR", "Brazil", "South America", true, "\uD83C\uDDE7\uD83C\uDDF7"),
    )

    private val testStatistics = CountryStatistics(
        visitedCount = 2,
        totalCount = 4,
        percentage = 50,
    )

    private val testRegions = listOf("Asia", "Europe", "North America", "South America")
    private val testVisitedCodes = setOf("FR", "BR")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getAllCountriesUseCase = mockk()
        getCountryStatisticsUseCase = mockk()
        getAllRegionsUseCase = mockk()
        getVisitedCountryCodesUseCase = mockk()
        worldMapGeometryLoader = mockk()
        savedStateHandle = SavedStateHandle()

        // Default behavior for mocks
        every { getAllCountriesUseCase(any(), any(), any()) } returns flowOf(testCountries)
        every { getCountryStatisticsUseCase() } returns flowOf(testStatistics)
        every { getAllRegionsUseCase() } returns flowOf(testRegions)
        every { getVisitedCountryCodesUseCase() } returns flowOf(testVisitedCodes)
        coEvery { worldMapGeometryLoader.load() } returns Result.Success(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun initViewModel() {
        viewModel = CountryListViewModel(
            getAllCountriesUseCase,
            getCountryStatisticsUseCase,
            getAllRegionsUseCase,
            getVisitedCountryCodesUseCase,
            worldMapGeometryLoader,
            savedStateHandle,
        )
    }

    @Test
    fun `initial state should be loading`() = runTest(testDispatcher) {
        initViewModel()
        val state = viewModel.state.value
        assertTrue(state.isLoading)
    }

    @Test
    fun `should load countries successfully`() = runTest(testDispatcher) {
        initViewModel()
        viewModel.state.test {
            // 1. Initial state
            assertEquals(CountryListState(isLoading = true), awaitItem())

            // 2. Loaded state
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(4, state.countries.size)
            assertEquals(2, state.visitedCount)
            assertEquals(4, state.totalCount)
            assertEquals(50, state.percentage)
            assertEquals(testRegions, state.allRegions)
            assertEquals(testVisitedCodes, state.visitedCountryCodes)
        }
    }

    @Test
    fun `should update search query in state and trigger new fetch after debounce`() = runTest(testDispatcher) {
        val query = "france"
        every { getAllCountriesUseCase(query, "All", false) } returns flowOf(listOf(testCountries[1]))

        initViewModel()

        viewModel.state.test {
            // Consume initial states
            skipItems(2)

            viewModel.onAction(CountryListAction.OnSearchQueryChange(query))

            // 3. State updates with new searchQuery, but countries are still the old ones (4)
            var state = awaitItem()
            assertEquals(query, state.searchQuery)
            assertEquals(4, state.countries.size)

            // 4. Advance time to trigger debounce (300ms)
            testScheduler.advanceTimeBy(301)

            // 5. New state with filtered countries
            state = awaitItem()
            assertEquals(1, state.countries.size)
            assertEquals("France", state.countries.first().name)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should filter countries by region and trigger new fetch`() = runTest(testDispatcher) {
        val region = "Europe"
        every { getAllCountriesUseCase("", region, false) } returns flowOf(listOf(testCountries[1]))

        initViewModel()

        viewModel.state.test {
            skipItems(2)

            viewModel.onAction(CountryListAction.OnRegionSelect(region))

            // No debounce for region, so we might get one or two emissions depending on how combine handles it
            // Typically: one where SR changed, then flatMapLatest triggers and emits new countries.

            var state = awaitItem()
            if (state.selectedRegion != region || state.countries.size != 1) {
                state = awaitItem()
            }

            assertEquals(region, state.selectedRegion)
            assertEquals(1, state.countries.size)
            assertEquals("France", state.countries.first().name)
        }
    }

    @Test
    fun `should filter visited countries only and trigger new fetch`() = runTest(testDispatcher) {
        every { getAllCountriesUseCase("", "All", true) } returns flowOf(testCountries.filter { it.visited })

        initViewModel()

        viewModel.state.test {
            skipItems(2)

            viewModel.onAction(CountryListAction.OnToggleShowOnlyVisited)

            var state = awaitItem()
            if (!state.showOnlyVisited || state.countries.size != 2) {
                state = awaitItem()
            }

            assertTrue(state.showOnlyVisited)
            assertEquals(2, state.countries.size)
            assertTrue(state.countries.all { it.visited })
        }
    }

    @Test
    fun `should navigate on country click`() = runTest(testDispatcher) {
        initViewModel()
        viewModel.onAction(CountryListAction.OnCountryClick("US"))

        viewModel.events.test {
            val event = awaitItem()
            assertTrue(event is CountryListEvent.NavigateToDetail)
        }
    }

    @Test
    fun `country fetch failure is surfaced as an error state`() = runTest(testDispatcher) {
        every { getAllCountriesUseCase(any(), any(), any()) } returns
            flow { throw IllegalStateException("network down") }

        initViewModel()

        val state = viewModel.state.first { it.error != null }

        assertFalse(state.isLoading)
        assertEquals(DataReadStatus.Unavailable, state.countriesStatus)
        assertTrue(state.countries.isEmpty())
        assertEquals(UiText.StringResource(R.string.error_load_countries), state.error)
    }

    @Test
    fun `statistics failure is not presented as zero visited countries`() = runTest(testDispatcher) {
        every { getCountryStatisticsUseCase() } returns flow { throw IllegalStateException("stats") }

        initViewModel()

        val state = viewModel.state.first { it.statisticsStatus == DataReadStatus.Unavailable }

        assertFalse(state.isLoading)
        assertEquals(UiText.StringResource(R.string.error_load_statistics), state.error)
        assertEquals(DataReadStatus.Ready, state.countriesStatus)
        assertEquals(testCountries, state.countries)
    }

    @Test
    fun `visited codes failure is not presented as no countries visited`() = runTest(testDispatcher) {
        every { getVisitedCountryCodesUseCase() } returns flow { throw IllegalStateException("visited") }

        initViewModel()

        val state = viewModel.state.first { it.visitedCodesStatus == DataReadStatus.Unavailable }

        assertEquals(UiText.StringResource(R.string.error_load_visited), state.error)
        assertEquals(DataReadStatus.Ready, state.countriesStatus)
        assertEquals(testCountries, state.countries)
    }

    @Test
    fun `regions failure is not presented as a successful empty region list`() = runTest(testDispatcher) {
        every { getAllRegionsUseCase() } returns flow { throw IllegalStateException("regions") }

        initViewModel()

        val state = viewModel.state.first { it.regionsStatus == DataReadStatus.Unavailable }

        assertEquals(UiText.StringResource(R.string.error_load_regions), state.error)
        assertEquals(DataReadStatus.Ready, state.countriesStatus)
        assertEquals(testCountries, state.countries)
    }

    @Test
    fun `clear error removes the snackbar message and keeps the failed read`() = runTest(testDispatcher) {
        every { getAllCountriesUseCase(any(), any(), any()) } returns
            flow { throw IllegalStateException("boom") }

        initViewModel()
        viewModel.state.first { it.error != null }

        viewModel.onAction(CountryListAction.OnClearError)
        advanceUntilIdle()

        assertNull(viewModel.state.value.error)
        assertEquals(DataReadStatus.Unavailable, viewModel.state.value.countriesStatus)
    }
}
