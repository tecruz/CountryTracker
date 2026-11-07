package com.tecruz.countrytracker.core.data.datasource

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.tecruz.countrytracker.core.domain.model.WorldMapViewport
import org.json.JSONObject

/**
 * Decodes `assets/world_map_paths.json` into country code / SVG path pairs.
 *
 * This object is the asset decoder, not the cache the UI reads.
 * MapRepositoryImpl owns retention and the failure policy. A successful decode is remembered here as well
 * so startup checks can see that the asset was read. The decode is committed only after
 * every entry is read, so a thrown error leaves the previous value untouched.
 */
object WorldMapPathData {

    const val VIEW_BOX_WIDTH = WorldMapViewport.WIDTH
    const val VIEW_BOX_HEIGHT = WorldMapViewport.HEIGHT

    private var _countryPaths: Map<String, String>? = null

    val countryPaths: Map<String, String>
        get() = _countryPaths ?: error(
            "WorldMapPathData not initialized. Call loadCountryPaths(context) first.",
        )

    val isLoaded: Boolean
        get() = _countryPaths != null

    /**
     * Load country paths from assets file.
     * Thread-safe and idempotent.
     */
    @Synchronized
    fun loadCountryPaths(context: Context): Int {
        if (_countryPaths != null) {
            return _countryPaths!!.size
        }

        val jsonString = context.assets
            .open("world_map_paths.json")
            .bufferedReader()
            .use { it.readText() }

        val jsonObject = JSONObject(jsonString)

        _countryPaths = buildMap {
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                put(key, jsonObject.getString(key))
            }
        }

        return _countryPaths!!.size
    }

    /**
     * Reset loaded state for testing purposes only.
     */
    @VisibleForTesting
    @Synchronized
    fun reset() {
        _countryPaths = null
    }
}
