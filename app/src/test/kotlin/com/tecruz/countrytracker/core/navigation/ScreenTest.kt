package com.tecruz.countrytracker.core.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenTest {

    @Test
    fun `CountryList has correct route`() {
        assertEquals("country_list", Screen.CountryList.route)
    }

    @Test
    fun `CountryDetail has correct route pattern`() {
        assertEquals("country_detail/{countryCode}", Screen.CountryDetail.route)
    }

    @Test
    fun `CountryDetail createRoute builds correct path`() {
        val route = Screen.CountryDetail.createRoute("US")
        assertEquals("country_detail/US", route)
    }

    @Test
    fun `CountryDetail createRoute handles any code`() {
        val route = Screen.CountryDetail.createRoute("FR")
        assertEquals("country_detail/FR", route)
    }
}
