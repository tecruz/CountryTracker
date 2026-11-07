package com.tecruz.countrytracker.core.domain.repository

import com.tecruz.countrytracker.core.domain.DataError
import com.tecruz.countrytracker.core.domain.EmptyResult
import com.tecruz.countrytracker.core.domain.Result
import com.tecruz.countrytracker.core.domain.model.CountryMapOutline

/**
 * Repository for world map outlines.
 *
 * The implementation owns the retained outlines for its own lifetime (a process-scoped
 * singleton in production). A successful read is cached. A failed read is not, so the
 * next call tries again. An empty list is a successful read of a map with no countries.
 */
interface MapRepository {
    /**
     * Reads country outlines from assets on the injected IO dispatcher.
     */
    suspend fun loadCountryOutlines(): Result<List<CountryMapOutline>, DataError.Local>

    /**
     * Preloads outlines for startup.
     * A missing asset is returned as a failure and does not throw, so splash can dismiss.
     */
    suspend fun loadMapData(): EmptyResult<DataError.Local>

    /**
     * Returns true after a successful [loadCountryOutlines] or [loadMapData].
     * A failed read leaves this false.
     */
    fun isMapDataLoaded(): Boolean
}
