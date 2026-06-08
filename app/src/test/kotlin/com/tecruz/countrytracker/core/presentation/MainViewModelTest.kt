package com.tecruz.countrytracker.core.presentation

import app.cash.turbine.test
import com.tecruz.countrytracker.core.domain.repository.MapRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mapRepository = mockk<MapRepository>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load map data if not loaded`() = runTest(testDispatcher) {
        // Given
        every { mapRepository.isMapDataLoaded() } returns false
        coEvery { mapRepository.loadMapData() } returns Unit

        // When
        val viewModel = MainViewModel(mapRepository)

        // Then
        viewModel.isInitialized.test {
            // Initial value from StateFlow (false)
            assertFalse(awaitItem())

            // Advance until idle to allow init block's launch to complete
            testScheduler.advanceUntilIdle()

            // New value should be true
            assertTrue(awaitItem())
            coVerify { mapRepository.loadMapData() }
        }
    }

    @Test
    fun `init should not load map data if already loaded`() = runTest(testDispatcher) {
        // Given
        every { mapRepository.isMapDataLoaded() } returns true

        // When
        val viewModel = MainViewModel(mapRepository)

        // Then
        viewModel.isInitialized.test {
            assertTrue(awaitItem())
            coVerify(exactly = 0) { mapRepository.loadMapData() }
        }
    }
}
