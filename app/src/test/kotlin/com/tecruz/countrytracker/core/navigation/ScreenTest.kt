package com.tecruz.countrytracker.core.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenTest {

    @Test
    fun `CountryDetail can be instantiated with code`() {
        val screen = Screen.CountryDetail("US")
        assertEquals("US", screen.countryCode)
    }
}
