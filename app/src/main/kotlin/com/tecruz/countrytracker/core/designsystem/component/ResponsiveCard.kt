package com.tecruz.countrytracker.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.tecruz.countrytracker.LocalWindowSizeClass
import com.tecruz.countrytracker.core.designsystem.preview.DevicePreviews
import com.tecruz.countrytracker.core.designsystem.preview.PreviewWrapper
import com.tecruz.countrytracker.core.util.isExpanded
import com.tecruz.countrytracker.core.util.isMedium

/**
 * A card component that adapts padding, spacing, elevation, and corner radius
 * based on the current window size class.
 *
 * @param windowSizeClass The current window size class
 * @param modifier Modifier for the Card
 * @param colors Card colors, defaults to CardDefaults
 * @param elevation Card elevation, defaults to responsive values
 * @param minHeight Minimum height for the card, defaults to responsive values
 * @param content Content composable inside the card
 */
@Composable
fun ResponsiveCard(
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation? = null,
    minHeight: Dp? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val (innerPadding, cornerRadius, defaultElevation) = responsiveCardValues(windowSizeClass)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cornerRadius),
        elevation = elevation ?: CardDefaults.cardElevation(defaultElevation = defaultElevation),
        colors = colors,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (minHeight != null) Modifier.heightIn(min = minHeight) else Modifier)
                .padding(innerPadding),
            content = content,
        )
    }
}

/**
 * Returns responsive card values (padding, corner radius, elevation) based on window size.
 */
private fun responsiveCardValues(windowSizeClass: WindowSizeClass): Triple<Dp, Dp, Dp> = when {
    windowSizeClass.isExpanded() -> Triple(24.dp, 24.dp, 4.dp)
    windowSizeClass.isMedium() -> Triple(20.dp, 20.dp, 3.dp)
    else -> Triple(16.dp, 16.dp, 2.dp)
}

@DevicePreviews
@Composable
private fun ResponsiveCardPreview() {
    PreviewWrapper {
        val windowSizeClass = LocalWindowSizeClass.current
        ResponsiveCard(
            windowSizeClass = windowSizeClass,
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Responsive Card Content",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}
