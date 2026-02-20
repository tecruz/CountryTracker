package com.tecruz.countrytracker

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass
import com.tecruz.countrytracker.core.designsystem.CountryTrackerTheme
import com.tecruz.countrytracker.core.navigation.CountryTrackerNavHost
import com.tecruz.countrytracker.features.countrylist.data.datasource.WorldMapPathData
import dagger.hilt.android.AndroidEntryPoint

/**
 * CompositionLocal to provide WindowSizeClass throughout the app for adaptive layouts.
 */
val LocalWindowSizeClass = staticCompositionLocalOf<WindowSizeClass> {
    error("WindowSizeClass not provided")
}

// Primary green color for status bar (matches PrimaryGreen in Color.kt)
private const val PRIMARY_GREEN = 0xFF00845D.toInt()

/**
 * Main Activity with Hilt dependency injection.
 * Entry point for the app.
 * Supports edge-to-edge display and adaptive layouts.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Enable edge-to-edge with themed status bar
        // Uses dark status bar style (light icons) with primary green color
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(PRIMARY_GREEN),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)

        // Eagerly load map path JSON from assets so it's ready before the Map tab is opened.
        // This is fast (~IO read) and avoids any delay when the user first views the map.
        if (!WorldMapPathData.isLoaded) {
            WorldMapPathData.loadCountryPaths(this)
        }

        setContent {
            val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                    Surface(
                        modifier = Modifier.Companion.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        CountryTrackerNavHost()
                    }
                }
            }
        }
    }
}
