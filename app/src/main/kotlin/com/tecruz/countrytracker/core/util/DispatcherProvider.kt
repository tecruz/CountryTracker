package com.tecruz.countrytracker.core.util

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * Wrapper around Kotlin coroutines dispatchers for dependency injection.
 *
 * Benefits:
 * - Testability: Allows injecting test dispatchers (e.g., TestDispatcher) in unit tests
 * - Centralization: Single source of truth for thread configuration across the app
 * - Flexibility: Easy to modify threading strategy without updating multiple files
 *
 * Usage:
 * ```
 * private val dispatchers: DispatcherProvider by inject()
 *
 * lifecycleScope.launch(dispatchers.io) {
 *     // I/O operations
 * }
 * ```
 */
class DispatcherProvider {

    val io: CoroutineContext = Dispatchers.IO
    val default: CoroutineContext = Dispatchers.Default
    val main: CoroutineContext = Dispatchers.Main
    val unconfined: CoroutineContext = Dispatchers.Unconfined
}
