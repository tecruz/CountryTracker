package com.tecruz.countrytracker.core.domain.repository

/**
 * Repository for handling world map data.
 */
interface MapRepository {
    /**
     * Loads the map path data from assets.
     */
    suspend fun loadMapData()

    /**
     * Returns true if map data is already loaded.
     */
    fun isMapDataLoaded(): Boolean
}
