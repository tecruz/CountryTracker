package com.tecruz.countrytracker.core.data.repository

import android.content.Context
import android.util.Log
import com.tecruz.countrytracker.core.data.datasource.WorldMapPathData
import com.tecruz.countrytracker.core.domain.DataError
import com.tecruz.countrytracker.core.domain.EmptyResult
import com.tecruz.countrytracker.core.domain.Result
import com.tecruz.countrytracker.core.domain.asEmptyResult
import com.tecruz.countrytracker.core.domain.model.CountryMapOutline
import com.tecruz.countrytracker.core.domain.repository.MapRepository
import com.tecruz.countrytracker.core.util.DispatcherProvider
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONException
import java.io.IOException

/**
 * Owns the retained world-map outlines for the lifetime of this instance.
 *
 * [WorldMapPathData] only decodes the asset. This class decides whether that decode
 * is kept. Failures clear the decoder and are not cached, so a later call can retry.
 * Callers never observe a partial outline map: the decoder commits only after the
 * whole file is read.
 */
class MapRepositoryImpl(private val context: Context, private val dispatchers: DispatcherProvider) : MapRepository {

    private val mutex = Mutex()
    private var cachedOutlines: List<CountryMapOutline>? = null

    override suspend fun loadCountryOutlines(): Result<List<CountryMapOutline>, DataError.Local> = mutex.withLock {
        cachedOutlines?.let { outlines ->
            return@withLock Result.Success(outlines)
        }
        val result = withContext(dispatchers.io) { readOutlines() }
        if (result is Result.Success) {
            cachedOutlines = result.data
        }
        result
    }

    override suspend fun loadMapData(): EmptyResult<DataError.Local> = loadCountryOutlines().asEmptyResult()

    override fun isMapDataLoaded(): Boolean = cachedOutlines != null

    private fun readOutlines(): Result<List<CountryMapOutline>, DataError.Local> = try {
        WorldMapPathData.loadCountryPaths(context)
        Result.Success(
            WorldMapPathData.countryPaths.map { (code, svgPath) ->
                CountryMapOutline(code = code, svgPath = svgPath)
            },
        )
    } catch (io: IOException) {
        Log.w(TAG, "World map asset could not be read", io)
        WorldMapPathData.reset()
        Result.Failure(DataError.Local.NOT_FOUND)
    } catch (json: JSONException) {
        Log.w(TAG, "World map asset could not be parsed", json)
        WorldMapPathData.reset()
        Result.Failure(DataError.Local.UNKNOWN)
    }

    private companion object {
        const val TAG = "MapRepository"
    }
}
