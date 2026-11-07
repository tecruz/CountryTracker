package com.tecruz.countrytracker.core.data.repository

import android.content.Context
import android.content.res.AssetManager
import com.tecruz.countrytracker.core.data.datasource.WorldMapPathData
import com.tecruz.countrytracker.core.domain.DataError
import com.tecruz.countrytracker.core.domain.Result
import com.tecruz.countrytracker.core.domain.model.CountryMapOutline
import com.tecruz.countrytracker.core.util.DispatcherProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import java.io.ByteArrayInputStream
import java.io.FileNotFoundException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class MapRepositoryImplTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val context = mockk<Context>()
    private val assets = mockk<AssetManager>()

    @Before
    fun setUp() {
        WorldMapPathData.reset()
        every { context.assets } returns assets
    }

    @After
    fun tearDown() {
        WorldMapPathData.reset()
        stopKoinQuietly()
    }

    @Test
    fun `missing asset is a not-found failure and is not cached`() = runTest(dispatcher) {
        every { assets.open("world_map_paths.json") } throws FileNotFoundException("missing")
        val repository = repository()

        val first = repository.loadCountryOutlines()
        val second = repository.loadCountryOutlines()

        assertEquals(DataError.Local.NOT_FOUND, (first as Result.Failure).error)
        assertEquals(DataError.Local.NOT_FOUND, (second as Result.Failure).error)
        assertFalse(repository.isMapDataLoaded())
        assertFalse(WorldMapPathData.isLoaded)
        verify(exactly = 2) { assets.open("world_map_paths.json") }
    }

    @Test
    fun `invalid json is an unknown failure and is not cached`() = runTest(dispatcher) {
        every { assets.open("world_map_paths.json") } answers {
            ByteArrayInputStream("{".toByteArray())
        }
        val repository = repository()

        val result = repository.loadCountryOutlines()

        assertEquals(DataError.Local.UNKNOWN, (result as Result.Failure).error)
        assertFalse(repository.isMapDataLoaded())
        assertFalse(WorldMapPathData.isLoaded)
    }

    @Test
    fun `a successful read is cached and an empty file is legitimate`() = runTest(dispatcher) {
        every { assets.open("world_map_paths.json") } answers {
            ByteArrayInputStream("{}".toByteArray())
        }
        val repository = repository()

        val first = repository.loadCountryOutlines()
        val second = repository.loadCountryOutlines()

        assertEquals(emptyList<CountryMapOutline>(), (first as Result.Success).data)
        assertEquals(emptyList<CountryMapOutline>(), (second as Result.Success).data)
        assertTrue(repository.isMapDataLoaded())
        verify(exactly = 1) { assets.open("world_map_paths.json") }
    }

    @Test
    fun `loadMapData returns a failure without throwing`() = runTest(dispatcher) {
        every { assets.open("world_map_paths.json") } throws FileNotFoundException("missing")
        val repository = repository()

        val result = repository.loadMapData()

        assertTrue(result is Result.Failure)
        assertFalse(repository.isMapDataLoaded())
    }

    @Test
    fun `parsed outlines keep every country from the asset`() = runTest(dispatcher) {
        every { assets.open("world_map_paths.json") } answers {
            ByteArrayInputStream("""{"US":"M0,0 L1,0 Z","FR":"M2,2 L3,2 Z"}""".toByteArray())
        }
        val repository = repository()

        val result = repository.loadCountryOutlines()

        assertEquals(
            setOf(
                CountryMapOutline("US", "M0,0 L1,0 Z"),
                CountryMapOutline("FR", "M2,2 L3,2 Z"),
            ),
            (result as Result.Success).data.toSet(),
        )
    }

    private fun stopKoinQuietly() {
        try {
            stopKoin()
        } catch (_: IllegalStateException) {
            // This test does not start Koin itself. Robolectric's application may have.
        }
    }

    private fun repository(): MapRepositoryImpl {
        val dispatchers = mockk<DispatcherProvider>()
        every { dispatchers.io } returns dispatcher
        return MapRepositoryImpl(context, dispatchers)
    }
}
