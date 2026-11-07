package com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap

import android.util.Log
import androidx.compose.ui.graphics.Path
import com.tecruz.countrytracker.core.domain.Result
import com.tecruz.countrytracker.core.domain.model.CountryMapOutline
import com.tecruz.countrytracker.core.domain.repository.MapRepository
import com.tecruz.countrytracker.core.util.DispatcherProvider
import com.tecruz.countrytracker.core.util.MapPathParser
import com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.model.CountryPathData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Loads and parses world-map geometry off the main thread.
 *
 * Cache ownership: this loader. In production it is a process-scoped Koin single,
 * so a successful parse survives navigation and a new country-list ViewModel.
 * The cache is dropped when [clear] is called, and it is never filled by a failure.
 * A parse error discards every path from that attempt, including paths that parsed,
 * so the map cannot render a silently partial world.
 */
class WorldMapGeometryLoader(
    private val mapRepository: MapRepository,
    private val pathParser: MapPathParser,
    private val dispatchers: DispatcherProvider,
) {
    private val mutex = Mutex()
    private var cachedPaths: List<CountryPathData>? = null

    suspend fun load(): Result<List<CountryPathData>, WorldMapFailure> = mutex.withLock {
        val cached = cachedPaths
        if (cached != null) {
            return@withLock Result.Success(cached)
        }
        when (val outlines = mapRepository.loadCountryOutlines()) {
            is Result.Failure -> Result.Failure(WorldMapFailure.AssetReadFailed)
            is Result.Success -> parseOutlines(outlines.data)
        }
    }

    /**
     * Drops a successful cache so the next [load] reads and parses again.
     * Safe to call when no [load] is in progress on this instance.
     */
    suspend fun clear() {
        mutex.withLock {
            cachedPaths = null
        }
    }

    private suspend fun parseOutlines(
        outlines: List<CountryMapOutline>,
    ): Result<List<CountryPathData>, WorldMapFailure> {
        val parsed = withContext(dispatchers.default) { buildPaths(outlines) }
        if (parsed == null) {
            return Result.Failure(WorldMapFailure.PathParseFailed)
        }
        cachedPaths = parsed
        return Result.Success(parsed)
    }

    private fun buildPaths(outlines: List<CountryMapOutline>): List<CountryPathData>? {
        val paths = ArrayList<CountryPathData>(outlines.size)
        outlines.forEach { outline ->
            val path = parseOrNull(outline.svgPath) ?: return null
            paths.add(CountryPathData(code = outline.code, path = path, bounds = path.getBounds()))
        }
        return paths
    }

    @Suppress("TooGenericExceptionCaught", "RethrowCaughtException", "InstanceOfCheckForException")
    private fun parseOrNull(pathData: String): Path? = try {
        pathParser.parse(pathData)
    } catch (failure: RuntimeException) {
        // PathParser reports bad SVG data as RuntimeException, which also covers
        // CancellationException. Cancellation must keep propagating.
        if (failure is CancellationException) throw failure
        Log.w(TAG, "World map path could not be parsed", failure)
        null
    }

    private companion object {
        const val TAG = "WorldMapGeometry"
    }
}
