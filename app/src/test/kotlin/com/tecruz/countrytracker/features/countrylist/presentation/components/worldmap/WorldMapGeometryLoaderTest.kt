package com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import com.tecruz.countrytracker.core.domain.DataError
import com.tecruz.countrytracker.core.domain.Result
import com.tecruz.countrytracker.core.domain.model.CountryMapOutline
import com.tecruz.countrytracker.core.domain.repository.MapRepository
import com.tecruz.countrytracker.core.util.DispatcherProvider
import com.tecruz.countrytracker.core.util.MapPathParser
import com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.model.CountryPathData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class WorldMapGeometryLoaderTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = mockk<MapRepository>()
    private val parser = mockk<MapPathParser>()

    @After
    fun tearDown() {
        try {
            stopKoin()
        } catch (_: IllegalStateException) {
            // Robolectric may have started Koin from the application.
        }
    }

    @Test
    fun `asset failure is not cached and does not parse`() = runTest(dispatcher) {
        coEvery { repository.loadCountryOutlines() } returns Result.Failure(DataError.Local.NOT_FOUND)
        val loader = loader()

        val first = loader.load()
        val second = loader.load()

        assertEquals(WorldMapFailure.AssetReadFailed, (first as Result.Failure).error)
        assertEquals(WorldMapFailure.AssetReadFailed, (second as Result.Failure).error)
        verify(exactly = 0) { parser.parse(any()) }
        coVerify(exactly = 2) { repository.loadCountryOutlines() }
    }

    @Test
    fun `a parse failure drops partial paths and is retried`() = runTest(dispatcher) {
        val good = path()
        coEvery { repository.loadCountryOutlines() } returns Result.Success(
            listOf(
                CountryMapOutline("US", "good"),
                CountryMapOutline("FR", "bad"),
            ),
        )
        every { parser.parse("good") } returns good
        every { parser.parse("bad") } throws IllegalArgumentException("bad path")
        val loader = loader()

        val failed = loader.load()
        assertEquals(WorldMapFailure.PathParseFailed, (failed as Result.Failure).error)

        every { parser.parse("bad") } returns path()
        val recovered = loader.load()

        assertEquals(listOf("US", "FR"), (recovered as Result.Success).data.map(CountryPathData::code))
        verify(exactly = 2) { parser.parse("good") }
        verify(exactly = 2) { parser.parse("bad") }
    }

    @Test
    fun `an empty outline list is a successful empty map and is cached`() = runTest(dispatcher) {
        coEvery { repository.loadCountryOutlines() } returns Result.Success(emptyList())
        val loader = loader()

        val first = loader.load()
        val second = loader.load()

        assertEquals(emptyList<CountryPathData>(), (first as Result.Success).data)
        assertEquals(emptyList<CountryPathData>(), (second as Result.Success).data)
        coVerify(exactly = 1) { repository.loadCountryOutlines() }
    }

    @Test
    fun `clear drops a successful cache`() = runTest(dispatcher) {
        coEvery { repository.loadCountryOutlines() } returns Result.Success(
            listOf(CountryMapOutline("US", "good")),
        )
        every { parser.parse("good") } returns path()
        val loader = loader()

        loader.load()
        loader.clear()
        loader.load()

        coVerify(exactly = 2) { repository.loadCountryOutlines() }
        verify(exactly = 2) { parser.parse("good") }
    }

    @Test
    fun `concurrent loads parse once`() = runTest(dispatcher) {
        coEvery { repository.loadCountryOutlines() } coAnswers {
            delay(100)
            Result.Success(listOf(CountryMapOutline("US", "good")))
        }
        every { parser.parse("good") } returns path()
        val loader = loader()

        val first = async { loader.load() }
        val second = async { loader.load() }
        testScheduler.advanceUntilIdle()

        assertTrue(first.await() is Result.Success)
        assertTrue(second.await() is Result.Success)
        verify(exactly = 1) { parser.parse("good") }
        coVerify(exactly = 1) { repository.loadCountryOutlines() }
    }

    private fun loader(): WorldMapGeometryLoader {
        val dispatchers = mockk<DispatcherProvider>()
        every { dispatchers.default } returns dispatcher
        every { dispatchers.io } returns dispatcher
        return WorldMapGeometryLoader(repository, parser, dispatchers)
    }

    private fun path(): Path = Path().apply { addRect(Rect(0f, 0f, 1f, 1f)) }
}
