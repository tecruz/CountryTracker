package com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.model

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path

/**
 * Data class holding a parsed country path with its bounding box for hit testing.
 */
data class CountryPathData(val code: String, val path: Path, val bounds: Rect)
