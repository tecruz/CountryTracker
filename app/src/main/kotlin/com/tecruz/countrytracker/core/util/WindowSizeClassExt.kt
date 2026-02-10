package com.tecruz.countrytracker.core.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass

/**
 * Extension functions for WindowSizeClass to provide responsive values
 * using the modern breakpoint-based API (isWidthAtLeastBreakpoint).
 */

/**
 * Returns whether the current window width is compact (phone).
 */
fun WindowSizeClass.isCompact(): Boolean = !isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

/**
 * Returns whether the current window width is medium (tablet portrait / foldable).
 */
fun WindowSizeClass.isMedium(): Boolean = isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) &&
    !isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

/**
 * Returns whether the current window width is expanded (tablet landscape / large).
 */
fun WindowSizeClass.isExpanded(): Boolean = isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

/**
 * Returns the number of grid columns appropriate for the current screen width.
 */
@Suppress("MagicNumber")
fun WindowSizeClass.gridColumns(): Int = when {
    isExpanded() -> 3
    isMedium() -> 2
    else -> 1
}

/**
 * Returns responsive horizontal padding based on screen size.
 */
fun WindowSizeClass.horizontalPadding(): Dp = when {
    isExpanded() -> 32.dp
    isMedium() -> 24.dp
    else -> 16.dp
}

/**
 * Returns responsive content padding based on screen size.
 */
fun WindowSizeClass.contentPadding(): Dp = when {
    isExpanded() -> 32.dp
    isMedium() -> 24.dp
    else -> 20.dp
}

/**
 * Returns responsive item spacing based on screen size.
 */
fun WindowSizeClass.itemSpacing(): Dp = when {
    isExpanded() -> 16.dp
    isMedium() -> 14.dp
    else -> 12.dp
}

/**
 * Returns the minimum touch target size for the current screen size.
 */
fun WindowSizeClass.minTouchTarget(): Dp = when {
    isExpanded() -> 56.dp
    isMedium() -> 52.dp
    else -> 48.dp
}
