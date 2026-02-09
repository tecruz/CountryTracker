package com.tecruz.countrytracker.core.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass

/**
 * Extension functions for WindowSizeClass to provide responsive values.
 */

/**
 * Returns whether the current window width is compact (phone).
 */
fun WindowSizeClass.isCompact(): Boolean = windowWidthSizeClass == WindowWidthSizeClass.COMPACT

/**
 * Returns whether the current window width is medium (tablet portrait / foldable).
 */
fun WindowSizeClass.isMedium(): Boolean = windowWidthSizeClass == WindowWidthSizeClass.MEDIUM

/**
 * Returns whether the current window width is expanded (tablet landscape / large).
 */
fun WindowSizeClass.isExpanded(): Boolean = windowWidthSizeClass == WindowWidthSizeClass.EXPANDED

/**
 * Returns the number of grid columns appropriate for the current screen width.
 */
@Suppress("MagicNumber")
fun WindowSizeClass.gridColumns(): Int = when (windowWidthSizeClass) {
    WindowWidthSizeClass.COMPACT -> 1
    WindowWidthSizeClass.MEDIUM -> 2
    WindowWidthSizeClass.EXPANDED -> 3
    else -> 1
}

/**
 * Returns responsive horizontal padding based on screen size.
 */
fun WindowSizeClass.horizontalPadding(): Dp = when (windowWidthSizeClass) {
    WindowWidthSizeClass.COMPACT -> 16.dp
    WindowWidthSizeClass.MEDIUM -> 24.dp
    WindowWidthSizeClass.EXPANDED -> 32.dp
    else -> 16.dp
}

/**
 * Returns responsive content padding based on screen size.
 */
fun WindowSizeClass.contentPadding(): Dp = when (windowWidthSizeClass) {
    WindowWidthSizeClass.COMPACT -> 20.dp
    WindowWidthSizeClass.MEDIUM -> 24.dp
    WindowWidthSizeClass.EXPANDED -> 32.dp
    else -> 20.dp
}

/**
 * Returns responsive item spacing based on screen size.
 */
fun WindowSizeClass.itemSpacing(): Dp = when (windowWidthSizeClass) {
    WindowWidthSizeClass.COMPACT -> 12.dp
    WindowWidthSizeClass.MEDIUM -> 14.dp
    WindowWidthSizeClass.EXPANDED -> 16.dp
    else -> 12.dp
}

/**
 * Returns the minimum touch target size for the current screen size.
 */
fun WindowSizeClass.minTouchTarget(): Dp = when (windowWidthSizeClass) {
    WindowWidthSizeClass.COMPACT -> 48.dp
    WindowWidthSizeClass.MEDIUM -> 52.dp
    WindowWidthSizeClass.EXPANDED -> 56.dp
    else -> 48.dp
}
