package com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap

import com.tecruz.countrytracker.core.data.datasource.WorldMapPathData
import com.tecruz.countrytracker.core.data.repository.MapRepositoryImpl
import com.tecruz.countrytracker.core.domain.Result
import com.tecruz.countrytracker.core.util.DispatcherProvider
import com.tecruz.countrytracker.core.util.SvgPathParser
import com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.model.CountryPathData
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import kotlin.system.measureTimeMillis

/**
 * Times the work that used to run inside the first composition of [WorldMapCanvas]
 * and the same work after it moved to [WorldMapGeometryLoader].
 *
 * The numbers are printed for the pull request. Assertions only check that both
 * paths produce the full map and that a cached open does not repeat the parse.
 */
@RunWith(RobolectricTestRunner::class)
class WorldMapColdOpenBenchmarkTest {

    @After
    fun tearDown() {
        WorldMapPathData.reset()
        try {
            stopKoin()
        } catch (_: IllegalStateException) {
            // Robolectric may have started Koin from the application.
        }
    }

    @Test
    fun coldMapOpen_recordsSynchronousCostAndCachedOpen() {
        val context = RuntimeEnvironment.getApplication()
        WorldMapPathData.reset()

        val synchronousMs = measureTimeMillis {
            if (!WorldMapPathData.isLoaded) {
                WorldMapPathData.loadCountryPaths(context)
            }
            WorldMapPathData.countryPaths.map { (code, pathData) ->
                val path = SvgPathParser.parse(pathData)
                CountryPathData(code, path, path.getBounds())
            }
        }
        val synchronousCount = WorldMapPathData.countryPaths.size
        WorldMapPathData.reset()

        val repository = MapRepositoryImpl(context, DispatcherProvider())
        val loader = WorldMapGeometryLoader(repository, SvgPathParser, DispatcherProvider())
        lateinit var loaded: Result<List<CountryPathData>, WorldMapFailure>
        val loaderMs = measureTimeMillis {
            loaded = runBlocking { loader.load() }
        }
        lateinit var cached: Result<List<CountryPathData>, WorldMapFailure>
        val cachedMs = measureTimeMillis {
            cached = runBlocking { loader.load() }
        }
        val loadedPaths = (loaded as Result.Success).data
        val cachedPaths = (cached as Result.Success).data

        println("COLD_MAP_SYNC_COMPOSITION_MS=$synchronousMs")
        println("COLD_MAP_LOADER_MS=$loaderMs")
        println("COLD_MAP_CACHED_MS=$cachedMs")
        println("COLD_MAP_COUNTRIES=$synchronousCount")

        assertEquals(synchronousCount, loadedPaths.size)
        assertTrue(synchronousCount > 100)
        assertTrue(loadedPaths === cachedPaths)
    }
}
