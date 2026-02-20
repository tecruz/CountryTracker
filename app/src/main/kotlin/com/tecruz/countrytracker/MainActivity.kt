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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.window.core.layout.WindowSizeClass
import com.tecruz.countrytracker.core.designsystem.CountryTrackerTheme
import com.tecruz.countrytracker.core.designsystem.PRIMARY_GREEN_ARGB
import com.tecruz.countrytracker.core.navigation.CountryTrackerNavHost
import com.tecruz.countrytracker.core.util.DispatcherProvider
import com.tecruz.countrytracker.features.countrylist.data.datasource.WorldMapPathData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * CompositionLocal to provide WindowSizeClass throughout the app for adaptive layouts.
 */
val LocalWindowSizeClass = staticCompositionLocalOf<WindowSizeClass> {
    error("WindowSizeClass not provided")
}

/**
 * Main Activity with Hilt dependency injection.
 * Entry point for the app.
 * Supports edge-to-edge display and adaptive layouts.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dispatchers: DispatcherProvider

    @Volatile
    private var isMapDataReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before super.onCreate() as required by the API.
        // The splash stays visible while map path data is loading on a background thread.
        val splashScreen = installSplashScreen()

        // Enable edge-to-edge with themed status bar
        // Uses dark status bar style (light icons) with primary green color
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(PRIMARY_GREEN_ARGB),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)

        // Load map path JSON from assets on a background coroutine during the splash animation.
        if (WorldMapPathData.isLoaded) {
            isMapDataReady = true
        } else {
            lifecycleScope.launch(dispatchers.io) {
                WorldMapPathData.loadCountryPaths(this@MainActivity)
                isMapDataReady = true
            }
        }

        // Keep the splash screen visible until the map data has finished loading.
        splashScreen.setKeepOnScreenCondition { !isMapDataReady }

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
