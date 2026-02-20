package com.tecruz.countrytracker.core.util

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton
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
 * @Inject lateinit var dispatchers: DispatcherProvider
 *
 * lifecycleScope.launch(dispatchers.io) {
 *     // I/O operations
 * }
 * ```
 */
@Singleton
class DispatcherProvider @Inject constructor() {

    val io: CoroutineContext = Dispatchers.IO
    val default: CoroutineContext = Dispatchers.Default
    val main: CoroutineContext = Dispatchers.Main
    val unconfined: CoroutineContext = Dispatchers.Unconfined
}
