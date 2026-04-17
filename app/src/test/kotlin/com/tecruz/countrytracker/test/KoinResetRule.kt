package com.tecruz.countrytracker.test

import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.core.context.stopKoin

class KoinResetRule : TestWatcher() {
    override fun starting(description: Description) {
        try {
            stopKoin()
        } catch (_: Exception) {
            // Koin not started, ignore
        }
    }

    override fun finished(description: Description) {
        try {
            stopKoin()
        } catch (_: Exception) {
            // Koin not started, ignore
        }
    }
}
