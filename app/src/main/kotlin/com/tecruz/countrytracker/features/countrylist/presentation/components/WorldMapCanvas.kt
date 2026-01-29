package com.tecruz.countrytracker.features.countrylist.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.tecruz.countrytracker.core.util.SvgPathParser
import com.tecruz.countrytracker.features.countrylist.data.datasource.WorldMapPathData

/**
 * Data class holding a parsed country path with its bounding box for hit testing.
 */
data class CountryPathData(val code: String, val path: Path, val bounds: Rect)

// Beautiful color palette for the map
private val OceanGradientStart = Color(0xFFB8D4E8)
private val OceanGradientMid = Color(0xFF9AC5E0)
private val OceanGradientEnd = Color(0xFF7FB3D8)
private val LandColor = Color(0xFFF8F9FA)
private val LandShadow = Color(0xFFE9ECEF)
private val VisitedColorLight = Color(0xFF66BB6A)
private val VisitedColorDark = Color(0xFF43A047)
private val BorderColor = Color(0xFFCED4DA)
private val VisitedBorderColor = Color(0xFF2E7D32)
private val CountryShadowColor = Color(0x33000000)

// Shimmer highlight positioning constants
private const val HIGHLIGHT_CENTER_X_RATIO = 0.3f
private const val HIGHLIGHT_CENTER_Y_RATIO = 0.2f
private const val HIGHLIGHT_RADIUS_RATIO = 0.5f

/**
 * A static world map canvas that displays visited countries in color.
 *
 * Features:
 * - Static view (no zoom, pan, or interaction)
 * - Beautiful ocean gradient background
 * - Shadows and depth effects for countries
 * - Visited countries highlighted in green
 * - Purely visual display
 *
 * @param visitedCountryCodes Set of ISO country codes that have been visited
 * @param modifier Modifier for the Canvas
 * @param unvisitedColor Color for unvisited countries
 * @param borderColor Color for country borders
 */
@Composable
fun WorldMapCanvas(
    visitedCountryCodes: Set<String>,
    modifier: Modifier = Modifier,
    unvisitedColor: Color = LandColor,
    borderColor: Color = BorderColor,
) {
    val context = LocalContext.current

    // Load map data from assets on first composition
    LaunchedEffect(Unit) {
        if (!WorldMapPathData.isLoaded) {
            WorldMapPathData.loadCountryPaths(context)
        }
    }

    // Parse all country paths once and cache them
    val countryPaths = remember {
        // Ensure data is loaded before accessing
        if (!WorldMapPathData.isLoaded) {
            WorldMapPathData.loadCountryPaths(context)
        }

        WorldMapPathData.countryPaths.mapNotNull { (code, pathData) ->
            runCatching {
                val path = SvgPathParser.parse(pathData)
                val bounds = path.getBounds()
                CountryPathData(code, path, bounds)
            }.getOrNull()
        }
    }

    // Build semantic description for accessibility and testing
    val visitedDescription = if (visitedCountryCodes.isEmpty()) {
        "World map with no visited countries"
    } else {
        "World map with visited countries: ${visitedCountryCodes.sorted().joinToString(", ")}"
    }

    Canvas(
        modifier = modifier
            .testTag("world_map")
            .semantics { contentDescription = visitedDescription },
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Calculate scale to fit the map while maintaining aspect ratio
        val scaleX = canvasWidth / WorldMapPathData.VIEW_BOX_WIDTH
        val scaleY = canvasHeight / WorldMapPathData.VIEW_BOX_HEIGHT
        val scale = minOf(scaleX, scaleY)

        // Center the map
        val scaledWidth = WorldMapPathData.VIEW_BOX_WIDTH * scale
        val scaledHeight = WorldMapPathData.VIEW_BOX_HEIGHT * scale
        val offsetX = (canvasWidth - scaledWidth) / 2
        val offsetY = (canvasHeight - scaledHeight) / 2

        // Draw ocean background
        drawOceanBackground(canvasWidth, canvasHeight)

        // Draw all countries
        countryPaths.forEach { countryData ->
            val isVisited = visitedCountryCodes.contains(countryData.code)
            val fillColor = if (isVisited) {
                Brush.verticalGradient(
                    colors = listOf(VisitedColorLight, VisitedColorDark),
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(unvisitedColor, LandShadow),
                )
            }
            val strokeColor = if (isVisited) VisitedBorderColor else borderColor

            // Draw shadow for depth
            drawCountryShadow(
                path = countryData.path,
                scale = scale,
                offsetX = offsetX,
                offsetY = offsetY,
            )

            // Draw country fill
            drawCountryFill(
                path = countryData.path,
                scale = scale,
                offsetX = offsetX,
                offsetY = offsetY,
                fillBrush = fillColor,
            )

            // Draw country border
            drawCountryBorder(
                path = countryData.path,
                scale = scale,
                offsetX = offsetX,
                offsetY = offsetY,
                borderColor = strokeColor,
            )
        }
    }
}

/**
 * Draw the ocean background with a beautiful multi-tone gradient.
 */
private fun DrawScope.drawOceanBackground(width: Float, height: Float) {
    // Draw a beautiful gradient for the ocean with depth
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                OceanGradientStart,
                OceanGradientMid,
                OceanGradientEnd,
            ),
            startY = 0f,
            endY = height,
        ),
        size = Size(width, height),
    )

    // Add subtle radial highlight overlay for shimmer effect
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.15f),
                Color.Transparent,
            ),
            center = Offset(width * HIGHLIGHT_CENTER_X_RATIO, height * HIGHLIGHT_CENTER_Y_RATIO),
            radius = width * HIGHLIGHT_RADIUS_RATIO,
        ),
        size = Size(width, height),
    )
}

/**
 * Draw country shadow for depth effect.
 */
private fun DrawScope.drawCountryShadow(path: Path, scale: Float, offsetX: Float, offsetY: Float) {
    val transformedPath = createTransformedPath(path, scale, offsetX + 2f, offsetY + 2f)

    drawPath(
        path = transformedPath,
        color = CountryShadowColor,
        style = Fill,
    )
}

/**
 * Draw a country fill with gradient.
 */
private fun DrawScope.drawCountryFill(path: Path, scale: Float, offsetX: Float, offsetY: Float, fillBrush: Brush) {
    val transformedPath = createTransformedPath(path, scale, offsetX, offsetY)

    drawPath(
        path = transformedPath,
        brush = fillBrush,
        style = Fill,
    )
}

/**
 * Draw a country border with enhanced styling.
 */
private fun DrawScope.drawCountryBorder(path: Path, scale: Float, offsetX: Float, offsetY: Float, borderColor: Color) {
    val transformedPath = createTransformedPath(path, scale, offsetX, offsetY)

    drawPath(
        path = transformedPath,
        color = borderColor,
        style = Stroke(width = 0.8f),
    )
}

/**
 * Create a transformed path by applying scale and offset.
 */
private fun createTransformedPath(path: Path, scale: Float, offsetX: Float, offsetY: Float): Path = Path().apply {
    addPath(path)
    transform(
        Matrix().apply {
            translate(offsetX, offsetY)
            scale(scale, scale)
        },
    )
}
