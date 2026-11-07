package com.tecruz.countrytracker.features.countrylist.presentation

import androidx.compose.runtime.Immutable
import com.tecruz.countrytracker.core.presentation.UiText
import com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.model.CountryPathData

/**
 * Geometry for the world map, loaded outside composition.
 *
 * [paths] is meaningful only when [status] is [DataReadStatus.Ready].
 * An empty [paths] list with [DataReadStatus.Ready] is a map that has no outlines.
 * [DataReadStatus.Unavailable] means the asset or the parse failed.
 */
@Immutable
data class WorldMapState(
    val status: DataReadStatus = DataReadStatus.Loading,
    val paths: List<CountryPathData> = emptyList(),
    val error: UiText? = null,
)
