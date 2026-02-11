package com.tecruz.countrytracker.core.designsystem.preview

import androidx.compose.ui.tooling.preview.Preview

/**
 * Multi-preview annotation for the three supported screen size classes:
 * - Phone (compact): 360x640 dp
 * - Foldable / tablet portrait (medium): 700x840 dp
 * - Tablet landscape (expanded): 1100x840 dp
 */
@Preview(
    name = "Phone",
    widthDp = 360,
    heightDp = 640,
    showBackground = true,
)
@Preview(
    name = "Foldable",
    widthDp = 700,
    heightDp = 840,
    showBackground = true,
)
@Preview(
    name = "Tablet",
    widthDp = 1100,
    heightDp = 840,
    showBackground = true,
)
annotation class DevicePreviews

/**
 * Multi-preview annotation combining light and dark themes for each device size.
 */
@DevicePreviews
@Preview(
    name = "Phone - Dark",
    widthDp = 360,
    heightDp = 640,
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    name = "Foldable - Dark",
    widthDp = 700,
    heightDp = 840,
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    name = "Tablet - Dark",
    widthDp = 1100,
    heightDp = 840,
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
)
annotation class DeviceThemePreviews
