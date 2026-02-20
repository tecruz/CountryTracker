package com.tecruz.countrytracker.features.countrylist.data.datasource

import android.content.Context
import androidx.annotation.VisibleForTesting
import org.json.JSONObject

/**
 * SVG path data loader for world map countries.
 * Uses lazy loading from assets to reduce memory footprint.
 */
object WorldMapPathData {

    const val VIEW_BOX_WIDTH = 1008f
    const val VIEW_BOX_HEIGHT = 651f

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
