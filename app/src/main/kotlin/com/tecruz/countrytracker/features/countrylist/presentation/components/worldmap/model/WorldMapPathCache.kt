package com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.model

/**
 * Process-level cache for parsed SVG paths so they survive recomposition and navigation.
 */
internal object WorldMapPathCache {
    @Volatile
    var paths: List<CountryPathData>? = null
}
