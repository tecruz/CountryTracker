package com.tecruz.countrytracker.test

import org.koin.core.context.stopKoin

object KoinTestHelper {
    fun resetKoin() {
        try {
            stopKoin()
        } catch (_: Exception) {
            // Koin not started, ignore
        }
    }
}
