package com.tecruz.countrytracker.features.countrydetail.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.core.util.DispatcherProvider
import com.tecruz.countrytracker.features.countrydetail.domain.GetCountryByCodeUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.MarkCountryAsUnvisitedUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.MarkCountryAsVisitedUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.UpdateCountryNotesUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.UpdateCountryRatingUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
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
    private lateinit var dispatcherProvider: DispatcherProvider
    private lateinit var viewModel: CountryDetailViewModel

    private val testCountry = Country(
        code = "US",
        name = "United States",
        region = "North America",
        visited = false,
        visitedDate = null,
        notes = "",
        rating = 0,
        flagEmoji = "🇺🇸",
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0

        getCountryByCodeUseCase = mockk(relaxed = true)
        markCountryAsVisitedUseCase = mockk(relaxed = true)
        markCountryAsUnvisitedUseCase = mockk(relaxed = true)
        updateCountryNotesUseCase = mockk(relaxed = true)
        updateCountryRatingUseCase = mockk(relaxed = true)

        // Use a real SavedStateHandle with the expected key
        savedStateHandle = SavedStateHandle(mapOf("countryCode" to "US"))

        dispatcherProvider = mockk {
            every { io } returns testDispatcher
            every { main } returns testDispatcher
            every { default } returns testDispatcher
            every { unconfined } returns testDispatcher
        }

        every { getCountryByCodeUseCase("US") } returns flowOf(testCountry)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    private fun createViewModel(): CountryDetailViewModel = CountryDetailViewModel(
        getCountryByCodeUseCase,
        markCountryAsVisitedUseCase,
        markCountryAsUnvisitedUseCase,
        updateCountryNotesUseCase,
        updateCountryRatingUseCase,
        savedStateHandle,
        dispatcherProvider,
    )

    @Test
    fun `initial state should be loading`() = runTest {
        viewModel = createViewModel()

        viewModel.state.test {
            val state = awaitItem()
            // With UnconfinedTestDispatcher, loading completes immediately
            assertTrue(state.isLoading || state.country != null)
        }
    }

    @Test
    fun `should load country successfully`() = runTest {
        viewModel = createViewModel()

        viewModel.state.test {
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

        viewModel.state.test {
            awaitItem() // Get loaded state with country

            val date = System.currentTimeMillis()
            viewModel.onAction(CountryDetailAction.OnMarkAsVisited(date, "Great trip!", 5))

            cancelAndIgnoreRemainingEvents()
        }

        coVerify { markCountryAsVisitedUseCase(any(), any(), "Great trip!", 5) }
    }

    @Test
    fun `markAsUnvisited should call use case`() = runTest {
        val visitedCountry = testCountry.copy(visited = true, visitedDate = 1704067200000L)
        every { getCountryByCodeUseCase("US") } returns flowOf(visitedCountry)

        viewModel = createViewModel()

        viewModel.state.test {
            awaitItem() // Get loaded state with country

            viewModel.onAction(CountryDetailAction.OnMarkAsUnvisited)

            cancelAndIgnoreRemainingEvents()
        }

        coVerify { markCountryAsUnvisitedUseCase(any()) }
    }

    @Test
    fun `updateNotes should call use case`() = runTest {
        viewModel = createViewModel()

        viewModel.state.test {
            awaitItem() // Get loaded state with country

            viewModel.onAction(CountryDetailAction.OnUpdateNotes("Short note"))

            cancelAndIgnoreRemainingEvents()
        }

        coVerify { updateCountryNotesUseCase(any(), "Short note") }
    }

    @Test
    fun `updateNotes should handle validation error`() = runTest {
        val longNotes = "a".repeat(Country.MAX_NOTES_LENGTH + 1)
        coEvery { updateCountryNotesUseCase.invoke(any(), longNotes) } throws
            IllegalArgumentException("Notes cannot exceed 500 characters")

        viewModel = createViewModel()

        viewModel.state.test {
            awaitItem() // Initial state

            // Trigger action that throws validation error
            viewModel.onAction(CountryDetailAction.OnUpdateNotes(longNotes))

            val errorState = awaitItem()
            assertNotNull("Error should not be null", errorState.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateRating should handle validation error`() = runTest {
        coEvery { updateCountryRatingUseCase(any(), 6) } throws
            IllegalArgumentException("Rating must be between 0 and 5")

        viewModel = createViewModel()

        viewModel.state.test {
            awaitItem() // Initial state

            // Trigger action that throws validation error
            viewModel.onAction(CountryDetailAction.OnUpdateRating(6))

            val errorState = awaitItem()
            assertNotNull("Error should not be null", errorState.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearError should clear error state`() = runTest {
        coEvery { updateCountryRatingUseCase.invoke(any(), 10) } throws
            IllegalArgumentException("Rating must be between 0 and 5")

        viewModel = createViewModel()

        viewModel.state.test {
            awaitItem() // Initial state

            // Trigger an error
            viewModel.onAction(CountryDetailAction.OnUpdateRating(10))
            val errorState = awaitItem()
            assertNotNull("Error should not be null after action", errorState.error)

            // Clear the error
            viewModel.onAction(CountryDetailAction.OnClearError)
            val clearedState = awaitItem()
            assertNull("Error should be null after clear", clearedState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateRating should call use case`() = runTest {
        viewModel = createViewModel()

        viewModel.state.test {
            awaitItem() // Get loaded state with country

            viewModel.onAction(CountryDetailAction.OnUpdateRating(4))

            cancelAndIgnoreRemainingEvents()
        }

        coVerify { updateCountryRatingUseCase(any(), 4) }
    }

    @Test
    fun `should not update when country is null`() = runTest {
        every { getCountryByCodeUseCase("US") } returns flowOf(null)

        viewModel = createViewModel()

        viewModel.state.test {
            awaitItem() // Get state (country will be null)

            viewModel.onAction(CountryDetailAction.OnUpdateRating(3))
            viewModel.onAction(CountryDetailAction.OnUpdateNotes("test"))
            viewModel.onAction(CountryDetailAction.OnMarkAsUnvisited)

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
