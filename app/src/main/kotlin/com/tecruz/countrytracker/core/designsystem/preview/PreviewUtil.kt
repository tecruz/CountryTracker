package com.tecruz.countrytracker.core.designsystem.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.window.core.layout.WindowSizeClass
import com.tecruz.countrytracker.LocalWindowSizeClass
import com.tecruz.countrytracker.core.designsystem.CountryTrackerTheme

/**
 * Wraps preview content with the app theme and provides a [WindowSizeClass]
 * derived from the preview's configured dimensions.
 *
 * @param widthDp The width of the preview surface in dp.
 * @param heightDp The height of the preview surface in dp.
 * @param content The composable preview content.
 */
@Composable
fun PreviewWrapper(widthDp: Int = 360, heightDp: Int = 640, content: @Composable () -> Unit) {
    val windowSizeClass = WindowSizeClass.compute(
        dpWidth = widthDp.toFloat(),
        dpHeight = heightDp.toFloat(),
    )
    CountryTrackerTheme(dynamicColor = false) {
        CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
            content()
        }
    }
}

/** Phone-sized preview wrapper (compact). */
@Composable
fun PhonePreview(content: @Composable () -> Unit) = PreviewWrapper(widthDp = 360, heightDp = 640, content = content)

/** Foldable/tablet-portrait preview wrapper (medium). */
@Composable
fun FoldablePreview(content: @Composable () -> Unit) = PreviewWrapper(widthDp = 700, heightDp = 840, content = content)

/** Tablet-landscape preview wrapper (expanded). */
@Composable
fun TabletPreview(content: @Composable () -> Unit) = PreviewWrapper(widthDp = 1100, heightDp = 840, content = content)
