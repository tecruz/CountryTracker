package com.tecruz.countrytracker.features.countrydetail.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.tecruz.countrytracker.core.navigation.Screen
import com.tecruz.countrytracker.features.countrydetail.domain.GetCountryByCodeUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.MarkCountryAsUnvisitedUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.MarkCountryAsVisitedUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.UpdateCountryNotesUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.UpdateCountryRatingUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.model.CountryDetail
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountryDetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var getCountryByCodeUseCase: GetCountryByCodeUseCase
    private lateinit var markCountryAsVisitedUseCase: MarkCountryAsVisitedUseCase
    private lateinit var markCountryAsUnvisitedUseCase: MarkCountryAsUnvisitedUseCase
    private lateinit var updateCountryNotesUseCase: UpdateCountryNotesUseCase
    private lateinit var updateCountryRatingUseCase: UpdateCountryRatingUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: CountryDetailViewModel

    private val testCountry = CountryDetail(
        code = "US",
        name = "United States",
        region = "North America",
        visited = false,
        visitedDate = null,
        notes = "",
        rating = 0,
        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8",
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCountryByCodeUseCase = mockk(relaxed = true)
        markCountryAsVisitedUseCase = mockk(relaxed = true)
        markCountryAsUnvisitedUseCase = mockk(relaxed = true)
        updateCountryNotesUseCase = mockk(relaxed = true)
        updateCountryRatingUseCase = mockk(relaxed = true)
        savedStateHandle = SavedStateHandle(mapOf(Screen.CountryDetail.ARG_COUNTRY_CODE to "US"))

        coEvery { getCountryByCodeUseCase("US") } returns testCountry
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): CountryDetailViewModel = CountryDetailViewModel(
        getCountryByCodeUseCase,
        markCountryAsVisitedUseCase,
        markCountryAsUnvisitedUseCase,
        updateCountryNotesUseCase,
        updateCountryRatingUseCase,
        savedStateHandle,
    )

    @Test
    fun `initial state should be loading`() {
        viewModel = createViewModel()
        val state = viewModel.uiState.value
        assertTrue(state.isLoading)
    }

    @Test
    fun `should load country successfully`() = runTest {
        viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNotNull(state.country)
            assertEquals("United States", state.country?.name)
            assertEquals("US", state.country?.code)
        }
    }

    @Test
    fun `markAsVisited should call use case`() = runTest {
        viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem() // Get loaded state with country

            val date = System.currentTimeMillis()
            viewModel.markAsVisited(date, "Great trip!", 5)

            cancelAndIgnoreRemainingEvents()
        }

        coVerify { markCountryAsVisitedUseCase(any(), any(), "Great trip!", 5) }
    }

    @Test
    fun `markAsUnvisited should call use case`() = runTest {
        val visitedCountry = testCountry.copy(visited = true, visitedDate = 1704067200000L)
        coEvery { getCountryByCodeUseCase("US") } returns visitedCountry

        viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem() // Get loaded state with country

            viewModel.markAsUnvisited()

            cancelAndIgnoreRemainingEvents()
        }

        coVerify { markCountryAsUnvisitedUseCase(any()) }
    }

    @Test
    fun `updateNotes should call use case`() = runTest {
        viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem() // Get loaded state with country

            viewModel.updateNotes("Short note")

            cancelAndIgnoreRemainingEvents()
        }

        coVerify { updateCountryNotesUseCase(any(), "Short note") }
    }

    @Test
    fun `updateNotes should handle validation error`() = runTest {
        val longNotes = "a".repeat(CountryDetail.MAX_NOTES_LENGTH + 1)
        coEvery { updateCountryNotesUseCase(any(), longNotes) } throws
            IllegalArgumentException("Notes cannot exceed 500 characters")

        viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem() // Get loaded state with country

            viewModel.updateNotes(longNotes)

            val state = awaitItem()
            assertNotNull(state.error)
            assertTrue(state.error!!.contains("500"))
        }
    }

    @Test
    fun `updateRating should call use case`() = runTest {
        viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem() // Get loaded state with country

            viewModel.updateRating(4)

            cancelAndIgnoreRemainingEvents()
        }

        coVerify { updateCountryRatingUseCase(any(), 4) }
    }

    @Test
    fun `updateRating should handle validation error`() = runTest {
        coEvery { updateCountryRatingUseCase(any(), 6) } throws
            IllegalArgumentException("Rating must be between 0 and 5")

        viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem() // Get loaded state with country

            viewModel.updateRating(6)

            val state = awaitItem()
            assertNotNull(state.error)
            assertTrue(state.error!!.contains("0 and 5"))
        }
    }

    @Test
    fun `clearError should clear error state`() = runTest {
        coEvery { updateCountryRatingUseCase(any(), 10) } throws IllegalArgumentException("Invalid rating")

        viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem() // Get loaded state with country

            // Trigger an error
            viewModel.updateRating(10)
            val errorState = awaitItem()
            assertNotNull(errorState.error)

            viewModel.clearError()

            val clearedState = awaitItem()
            assertNull(clearedState.error)
        }
    }

    @Test
    fun `should handle use case error gracefully`() = runTest {
        coEvery { getCountryByCodeUseCase("US") } throws RuntimeException("Network error")

        viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNotNull(state.error)
            assertNull(state.country)
        }
    }

    @Test
    fun `should not update when country is null`() = runTest {
        coEvery { getCountryByCodeUseCase("US") } returns null

        viewModel = createViewModel()

        viewModel.uiState.test {
            awaitItem() // Get state (country will be null)

            viewModel.updateRating(3)
            viewModel.updateNotes("test")
            viewModel.markAsUnvisited()

            // Should not crash and should not call use cases
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 0) {
            updateCountryRatingUseCase(any(), any())
            updateCountryNotesUseCase(any(), any())
            markCountryAsUnvisitedUseCase(any())
        }
    }
}
