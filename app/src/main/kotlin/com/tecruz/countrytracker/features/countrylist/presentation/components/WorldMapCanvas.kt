package com.tecruz.countrytracker.features.countrylist.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.tecruz.countrytracker.core.designsystem.CountryShadow
import com.tecruz.countrytracker.core.designsystem.LandColor
import com.tecruz.countrytracker.core.designsystem.LandShadow
import com.tecruz.countrytracker.core.designsystem.MapBorder
import com.tecruz.countrytracker.core.designsystem.MapVisitedBorder
import com.tecruz.countrytracker.core.designsystem.MapVisitedDark
import com.tecruz.countrytracker.core.designsystem.MapVisitedLight
import com.tecruz.countrytracker.core.designsystem.OceanGradientEnd
import com.tecruz.countrytracker.core.designsystem.OceanGradientMid
import com.tecruz.countrytracker.core.designsystem.OceanGradientStart
import com.tecruz.countrytracker.core.util.SvgPathParser
import com.tecruz.countrytracker.features.countrylist.data.datasource.WorldMapPathData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Data class holding a parsed country path with its bounding box for hit testing.
 */
data class CountryPathData(val code: String, val path: Path, val bounds: Rect)

/**
 * Process-level cache for parsed SVG paths so they survive recomposition and navigation.
 */
private object WorldMapPathCache {
    @Volatile
    var paths: List<CountryPathData>? = null
}

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
 * - Async path parsing on background thread
 * - Cached transformed paths via drawWithCache
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
    borderColor: Color = MapBorder,
) {
    val context = LocalContext.current

    // Use cached paths if available, otherwise start with empty list
    var countryPaths by remember { mutableStateOf(WorldMapPathCache.paths ?: emptyList()) }

    // Parse paths once and cache for the lifetime of the process
    LaunchedEffect(Unit) {
        if (WorldMapPathCache.paths != null) {
            countryPaths = WorldMapPathCache.paths!!
            return@LaunchedEffect
        }

        val parsed = withContext(Dispatchers.Default) {
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
        WorldMapPathCache.paths = parsed
        countryPaths = parsed
    }

    // Build semantic description for accessibility and testing
    val visitedDescription = if (visitedCountryCodes.isEmpty()) {
        "World map with no visited countries"
    } else {
        "World map with visited countries: ${visitedCountryCodes.sorted().joinToString(", ")}"
    }

    Box(
        modifier = modifier
            .testTag("world_map")
            .semantics { contentDescription = visitedDescription }
            .drawWithCache {
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

                // Pre-compute transformed paths (runs only on size change)
                data class TransformedCountry(val code: String, val fillPath: Path, val shadowPath: Path)

                val transformedCountries = countryPaths.map { countryData ->
                    val fillPath = Path().apply {
                        addPath(countryData.path)
                        transform(
                            Matrix().apply {
                                translate(offsetX, offsetY)
                                scale(scale, scale)
                            },
                        )
                    }
                    val shadowPath = Path().apply {
                        addPath(countryData.path)
                        transform(
                            Matrix().apply {
                                translate(offsetX + 2f, offsetY + 2f)
                                scale(scale, scale)
                            },
                        )
                    }
                    TransformedCountry(countryData.code, fillPath, shadowPath)
                }

                // Pre-compute ocean background
                val oceanBrush = Brush.verticalGradient(
                    colors = listOf(OceanGradientStart, OceanGradientMid, OceanGradientEnd),
                    startY = 0f,
                    endY = canvasHeight,
                )
                val highlightBrush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.15f), Color.Transparent),
                    center = Offset(
                        canvasWidth * HIGHLIGHT_CENTER_X_RATIO,
                        canvasHeight * HIGHLIGHT_CENTER_Y_RATIO,
                    ),
                    radius = canvasWidth * HIGHLIGHT_RADIUS_RATIO,
                )

                onDrawBehind {
                    // Draw ocean background
                    drawRect(brush = oceanBrush, size = Size(canvasWidth, canvasHeight))
                    drawRect(brush = highlightBrush, size = Size(canvasWidth, canvasHeight))

                    // Draw all countries
                    transformedCountries.forEach { country ->
                        val isVisited = visitedCountryCodes.contains(country.code)
                        val fillBrush = if (isVisited) {
                            Brush.verticalGradient(
                                colors = listOf(MapVisitedLight, MapVisitedDark),
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(unvisitedColor, LandShadow),
                            )
                        }
                        val strokeColor = if (isVisited) MapVisitedBorder else borderColor

                        // Draw shadow
                        drawPath(path = country.shadowPath, color = CountryShadow, style = Fill)

                        // Draw fill
                        drawPath(path = country.fillPath, brush = fillBrush, style = Fill)

                        // Draw border
                        drawPath(path = country.fillPath, color = strokeColor, style = Stroke(width = 0.8f))
                    }
                }
            },
    )
}
