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
import com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.WorldMapFailure
import com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.WorldMapGeometryLoader
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountryListRecoveryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getAllCountriesUseCase = mockk<GetAllCountriesUseCase>()
    private val getCountryStatisticsUseCase = mockk<GetCountryStatisticsUseCase>()
    private val getAllRegionsUseCase = mockk<GetAllRegionsUseCase>()
    private val getVisitedCountryCodesUseCase = mockk<GetVisitedCountryCodesUseCase>()
    private val worldMapGeometryLoader = mockk<WorldMapGeometryLoader>()

    private val france = CountryListItem("FR", "France", "Europe", true, "\uD83C\uDDEB\uD83C\uDDF7")
    private val countries = listOf(
        CountryListItem("US", "United States", "North America", false, "\uD83C\uDDFA\uD83C\uDDF8"),
        france,
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getAllCountriesUseCase(any(), any(), any()) } returns flowOf(countries)
        every { getCountryStatisticsUseCase() } returns flowOf(CountryStatistics(1, 2, 50))
        every { getAllRegionsUseCase() } returns flowOf(listOf("Europe", "North America"))
        every { getVisitedCountryCodesUseCase() } returns flowOf(setOf("FR"))
        coEvery { worldMapGeometryLoader.load() } returns Result.Success(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `an empty country list is a successful empty result`() = runTest(testDispatcher) {
        every { getAllCountriesUseCase(any(), any(), any()) } returns flowOf(emptyList())
        val viewModel = viewModel()

        val state = viewModel.state.first { !it.isLoading }

        assertEquals(DataReadStatus.Ready, state.countriesStatus)
        assertTrue(state.countries.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `retry recovers a failed country read and keeps filters`() = runTest(testDispatcher) {
        var attempts = 0
        every { getAllCountriesUseCase("fra", "Europe", true) } answers {
            flow {
                attempts += 1
                if (attempts == 1) {
                    error("database")
                }
                emit(listOf(france))
            }
        }
        val savedStateHandle = SavedStateHandle(
            mapOf(
                "search_query" to "fra",
                "selected_region" to "Europe",
                "show_only_visited" to true,
            ),
        )
        val viewModel = viewModel(savedStateHandle)

        val failed = viewModel.state.first { it.countriesStatus == DataReadStatus.Unavailable }
        assertEquals("fra", failed.searchQuery)
        assertEquals("Europe", failed.selectedRegion)
        assertTrue(failed.showOnlyVisited)
        assertEquals(UiText.StringResource(R.string.error_load_countries), failed.error)

        viewModel.onAction(CountryListAction.OnRetry)
        val recovered = viewModel.state.first {
            it.countriesStatus == DataReadStatus.Ready && it.countries.isNotEmpty()
        }

        assertEquals(listOf(france), recovered.countries)
        assertNull(recovered.error)
        assertEquals("fra", recovered.searchQuery)
        assertEquals("Europe", recovered.selectedRegion)
        assertTrue(recovered.showOnlyVisited)
        assertEquals("fra", savedStateHandle.get<String>("search_query"))
        assertEquals("Europe", savedStateHandle.get<String>("selected_region"))
        assertEquals(true, savedStateHandle.get<Boolean>("show_only_visited"))
    }

    @Test
    fun `filters saved before process death are restored into the next read`() = runTest(testDispatcher) {
        every { getAllCountriesUseCase("fra", "Europe", true) } returns flowOf(listOf(france))
        val savedStateHandle = SavedStateHandle(
            mapOf(
                "search_query" to "fra",
                "selected_region" to "Europe",
                "show_only_visited" to true,
            ),
        )
        val viewModel = viewModel(savedStateHandle)

        val state = viewModel.state.first { !it.isLoading }

        assertEquals("fra", state.searchQuery)
        assertEquals("Europe", state.selectedRegion)
        assertTrue(state.showOnlyVisited)
        assertEquals(listOf(france), state.countries)
        verify { getAllCountriesUseCase("fra", "Europe", true) }
    }

    @Test
    fun `a new region cancels the in-flight country load`() = runTest(testDispatcher) {
        val europeStarted = CompletableDeferred<Unit>()
        val europeCancelled = CompletableDeferred<Unit>()
        every { getAllCountriesUseCase("", "Europe", false) } returns flow {
            europeStarted.complete(Unit)
            try {
                awaitCancellation()
            } finally {
                europeCancelled.complete(Unit)
            }
        }
        val viewModel = viewModel()
        viewModel.state.first { !it.isLoading }

        viewModel.onAction(CountryListAction.OnRegionSelect("Europe"))
        europeStarted.await()
        viewModel.onAction(CountryListAction.OnRegionSelect("Asia"))
        europeCancelled.await()

        val state = viewModel.state.first { it.selectedRegion == "Asia" && it.countriesStatus == DataReadStatus.Ready }
        assertEquals("Asia", state.selectedRegion)
        assertEquals(countries, state.countries)
    }

    @Test
    fun `map asset failure is reported and retry loads the geometry`() = runTest(testDispatcher) {
        var attempts = 0
        coEvery { worldMapGeometryLoader.load() } coAnswers {
            attempts += 1
            if (attempts == 1) {
                Result.Failure(WorldMapFailure.AssetReadFailed)
            } else {
                Result.Success(emptyList())
            }
        }
        val viewModel = viewModel()

        viewModel.mapState.test {
            assertEquals(DataReadStatus.Loading, awaitItem().status)
            val failed = awaitItem()
            assertEquals(DataReadStatus.Unavailable, failed.status)
            assertEquals(UiText.StringResource(R.string.error_load_map), failed.error)
            assertTrue(failed.paths.isEmpty())

            viewModel.onAction(CountryListAction.OnRetry)
            assertEquals(DataReadStatus.Loading, awaitItem().status)
            val recovered = awaitItem()
            assertEquals(DataReadStatus.Ready, recovered.status)
            assertNull(recovered.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `map parse failure uses the parse message`() = runTest(testDispatcher) {
        coEvery { worldMapGeometryLoader.load() } returns Result.Failure(WorldMapFailure.PathParseFailed)
        val viewModel = viewModel()

        val failed = viewModel.mapState.first { it.status == DataReadStatus.Unavailable }

        assertEquals(UiText.StringResource(R.string.error_parse_map), failed.error)
        coVerify(exactly = 1) { worldMapGeometryLoader.load() }
    }

    private fun viewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) = CountryListViewModel(
        getAllCountriesUseCase,
        getCountryStatisticsUseCase,
        getAllRegionsUseCase,
        getVisitedCountryCodesUseCase,
        worldMapGeometryLoader,
        savedStateHandle,
    )
}
