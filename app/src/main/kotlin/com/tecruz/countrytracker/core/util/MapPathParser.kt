package com.tecruz.countrytracker.core.util

import androidx.compose.ui.graphics.Path

/**
 * Parses one SVG path string into a Compose [Path].
 * Injected so map loading can fail closed when a path cannot be parsed.
 */
fun interface MapPathParser {
    fun parse(pathData: String): Path
}
