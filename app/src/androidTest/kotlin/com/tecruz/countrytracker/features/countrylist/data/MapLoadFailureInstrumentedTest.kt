package com.tecruz.countrytracker.features.countrylist.data

import android.content.Context
import android.content.res.AssetManager
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tecruz.countrytracker.core.data.datasource.WorldMapPathData
import com.tecruz.countrytracker.core.data.repository.MapRepositoryImpl
import com.tecruz.countrytracker.core.domain.DataError
import com.tecruz.countrytracker.core.domain.Result
import com.tecruz.countrytracker.core.domain.model.CountryMapOutline
import com.tecruz.countrytracker.core.domain.repository.MapRepository
import com.tecruz.countrytracker.core.util.DispatcherProvider
import com.tecruz.countrytracker.core.util.MapPathParser
import com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.WorldMapFailure
import com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.WorldMapGeometryLoader
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.FileNotFoundException

/**
 * Runs the asset and parse failure paths on device so JaCoCo records the catch
 * handlers. Unit tests cover the same behavior, but Robolectric execution is not
 * always present in the combined JaCoCo report.
 */
@RunWith(AndroidJUnit4::class)
class MapLoadFailureInstrumentedTest {

    @After
    fun restoreLoadedAsset() {
        WorldMapPathData.reset()
        val context = ApplicationProvider.getApplicationContext<Context>()
        runBlocking {
            MapRepositoryImpl(context, DispatcherProvider()).loadMapData()
        }
    }

    @Test
    fun missingAsset_isNotCached() = runBlocking {
        val repository = repositoryThatOpens { throw FileNotFoundException("missing") }

        val result = repository.loadCountryOutlines()

        assertEquals(DataError.Local.NOT_FOUND, (result as Result.Failure).error)
        assertFalse(repository.isMapDataLoaded())
        assertFalse(WorldMapPathData.isLoaded)
    }

    @Test
    fun invalidJson_isNotCached() = runBlocking {
        val repository = repositoryThatOpens { ByteArrayInputStream("{".toByteArray()) }

        val result = repository.loadCountryOutlines()

        assertEquals(DataError.Local.UNKNOWN, (result as Result.Failure).error)
        assertFalse(WorldMapPathData.isLoaded)
    }

    @Test
    fun pathParseFailure_discardsPartialGeometry() = runBlocking {
        val repository = mockk<MapRepository>()
        coEvery { repository.loadCountryOutlines() } returns Result.Success(
            listOf(
                CountryMapOutline("US", "good"),
                CountryMapOutline("FR", "bad"),
            ),
        )
        val parser = MapPathParser { data ->
            if (data == "bad") {
                throw IllegalArgumentException("bad path")
            }
            Path().apply { addRect(Rect(0f, 0f, 1f, 1f)) }
        }
        val loader = WorldMapGeometryLoader(repository, parser, DispatcherProvider())

        val failed = loader.load()
        assertEquals(WorldMapFailure.PathParseFailed, (failed as Result.Failure).error)

        coEvery { repository.loadCountryOutlines() } returns Result.Success(
            listOf(CountryMapOutline("US", "good")),
        )
        val recovered = loader.load()
        assertTrue(recovered is Result.Success)
    }

    private fun repositoryThatOpens(open: () -> java.io.InputStream): MapRepositoryImpl {
        WorldMapPathData.reset()
        val context = mockk<Context>()
        val assets = mockk<AssetManager>()
        every { context.assets } returns assets
        every { assets.open("world_map_paths.json") } answers { open() }
        return MapRepositoryImpl(context, DispatcherProvider())
    }
}
