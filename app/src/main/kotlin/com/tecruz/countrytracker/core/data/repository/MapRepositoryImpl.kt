package com.tecruz.countrytracker.core.data.repository

import android.content.Context
import com.tecruz.countrytracker.core.data.datasource.WorldMapPathData
import com.tecruz.countrytracker.core.domain.repository.MapRepository
import com.tecruz.countrytracker.core.util.DispatcherProvider
import kotlinx.coroutines.withContext

/**
 * Implementation of [MapRepository] that uses [WorldMapPathData] to load data from assets.
 */
class MapRepositoryImpl(private val context: Context, private val dispatchers: DispatcherProvider) : MapRepository {

    override suspend fun loadMapData() = withContext(dispatchers.io) {
        if (!WorldMapPathData.isLoaded) {
            WorldMapPathData.loadCountryPaths(context)
        }
    }

    override fun isMapDataLoaded(): Boolean = WorldMapPathData.isLoaded
}
