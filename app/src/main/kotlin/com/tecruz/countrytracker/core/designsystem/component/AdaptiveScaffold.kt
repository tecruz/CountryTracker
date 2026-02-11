package com.tecruz.countrytracker.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.tecruz.countrytracker.LocalWindowSizeClass
import com.tecruz.countrytracker.core.designsystem.preview.DevicePreviews
import com.tecruz.countrytracker.core.designsystem.preview.PreviewWrapper
import com.tecruz.countrytracker.core.util.isCompact

/**
 * AdaptiveScaffold provides a responsive scaffold that adapts navigation
 * and content layout based on the current WindowSizeClass.
 *
 * - Compact: Content fills entire screen (navigation handled externally, e.g., bottom nav)
 * - Medium/Expanded: Optionally shows a navigation rail on the left side
 *
 * @param windowSizeClass The current window size class
 * @param modifier Modifier for the root layout
 * @param navigationRail Optional navigation rail composable for medium/expanded screens
 * @param content Main content composable
 */
@Composable
fun AdaptiveScaffold(
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier,
    navigationRail: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    if (windowSizeClass.isCompact() || navigationRail == null) {
        // Compact: Full-width content, no navigation rail
        Box(modifier = modifier.fillMaxSize()) {
            content()
        }
    } else {
        // Medium/Expanded: Navigation rail on left, content on right
        Row(modifier = modifier.fillMaxSize()) {
            navigationRail()
            Box(modifier = Modifier.weight(1f)) {
                content()
            }
        }
    }
}

@DevicePreviews
@Composable
private fun AdaptiveScaffoldPreview() {
    PreviewWrapper {
        val windowSizeClass = LocalWindowSizeClass.current
        AdaptiveScaffold(
            windowSizeClass = windowSizeClass,
            navigationRail = {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(80.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Rail")
                }
            },
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("Content Area")
            }
        }
    }
}
