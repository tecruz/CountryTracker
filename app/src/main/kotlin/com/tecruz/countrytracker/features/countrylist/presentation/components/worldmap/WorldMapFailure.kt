package com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap

import com.tecruz.countrytracker.core.domain.Error

/**
 * Why the world map geometry could not be produced.
 * Asset and parse failures stay distinct so the UI can name the failed step.
 */
sealed interface WorldMapFailure : Error {
    data object AssetReadFailed : WorldMapFailure

    data object PathParseFailed : WorldMapFailure
}
