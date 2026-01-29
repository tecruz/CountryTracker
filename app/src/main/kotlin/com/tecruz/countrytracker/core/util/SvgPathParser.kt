package com.tecruz.countrytracker.core.util

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposePath
import androidx.core.graphics.PathParser

/**
 * Utility object to parse SVG path strings into Compose Path objects.
 * Uses Android SDK's PathParser for robust SVG path parsing.
 */
object SvgPathParser {

    /**
     * Parse an SVG path string into a Compose Path.
     * @param pathData The SVG path data string (e.g., "M 10,10 L 20,20 Z")
     * @return A Compose Path object representing the parsed path
     */
    fun parse(pathData: String): Path {
        // Use Android's PathParser to parse the SVG path data
        val androidPath = PathParser.createPathFromPathData(pathData)
        // Convert Android Path to Compose Path
        return androidPath.asComposePath()
    }
}
