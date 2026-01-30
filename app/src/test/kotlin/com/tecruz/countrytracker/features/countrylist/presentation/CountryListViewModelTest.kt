package com.tecruz.countrytracker.features.countrylist.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.tecruz.countrytracker.features.countrylist.domain.CountryStatistics
import com.tecruz.countrytracker.features.countrylist.domain.GetAllCountriesUseCase
import com.tecruz.countrytracker.features.countrylist.domain.GetCountryStatisticsUseCase
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountryListViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var getAllCountriesUseCase: GetAllCountriesUseCase
    private lateinit var getCountryStatisticsUseCase: GetCountryStatisticsUseCase
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

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getAllCountriesUseCase = mockk()
        getCountryStatisticsUseCase = mockk()
        savedStateHandle = SavedStateHandle()

        every { getAllCountriesUseCase() } returns flowOf(testCountries)
        every { getCountryStatisticsUseCase() } returns flowOf(testStatistics)

        viewModel = CountryListViewModel(getAllCountriesUseCase, getCountryStatisticsUseCase, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be loading`() {
        val state = viewModel.uiState.value
        assertTrue(state.isLoading)
    }

    @Test
    fun `should load countries successfully`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(4, state.countries.size)
            assertEquals(2, state.visitedCount)
            assertEquals(4, state.totalCount)
            assertEquals(50, state.percentage)
        }
    }

    @Test
    fun `should update search query in state`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Initial loaded state

            viewModel.updateSearchQuery("france")

            val state = awaitItem()
            assertEquals("france", state.searchQuery)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should filter countries by region`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Initial loaded state

            viewModel.selectRegion("Europe")

            val state = awaitItem()
            assertEquals("Europe", state.selectedRegion)
            assertEquals(1, state.countries.size)
            assertEquals("France", state.countries.first().name)
        }
    }

    @Test
    fun `should filter visited countries only`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Initial loaded state

            viewModel.toggleShowOnlyVisited()

            val state = awaitItem()
            assertTrue(state.showOnlyVisited)
            assertEquals(2, state.countries.size)
            assertTrue(state.countries.all { it.visited })
        }
    }

    @Test
    fun `should toggle visited filter off`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Initial loaded state

            viewModel.toggleShowOnlyVisited() // Turn on
            awaitItem()

            viewModel.toggleShowOnlyVisited() // Turn off

            val state = awaitItem()
            assertFalse(state.showOnlyVisited)
            assertEquals(4, state.countries.size)
        }
    }

    @Test
    fun `should extract unique regions from countries`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(4, state.allRegions.size)
            assertTrue(state.allRegions.containsAll(listOf("Asia", "Europe", "North America", "South America")))
        }
    }

    @Test
    fun `should combine multiple filters correctly`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Initial loaded state

            viewModel.selectRegion("South America")
            awaitItem()

            viewModel.toggleShowOnlyVisited()

            val state = awaitItem()
            assertEquals(1, state.countries.size)
            assertEquals("Brazil", state.countries.first().name)
        }
    }

    @Test
    fun `should reset region filter to All`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Initial loaded state

            viewModel.selectRegion("Europe")
            val filteredState = awaitItem()
            assertEquals(1, filteredState.countries.size)

            viewModel.selectRegion("All")

            val state = awaitItem()
            assertEquals("All", state.selectedRegion)
            assertEquals(4, state.countries.size)
        }
    }
}
