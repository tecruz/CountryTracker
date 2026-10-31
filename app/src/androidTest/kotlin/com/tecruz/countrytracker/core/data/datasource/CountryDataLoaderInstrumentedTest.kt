package com.tecruz.countrytracker.core.data.datasource

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.io.InputStream

/**
 * Instrumented coverage for [CountryDataLoader].
 *
 * Covers the successful asset parse as well as the unreadable-asset and
 * malformed-JSON fallbacks.
 */
@RunWith(AndroidJUnit4::class)
class CountryDataLoaderInstrumentedTest {

    @Test
    fun loadsCountriesFromAssets() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val countries = CountryDataLoader.loadCountriesFromAssets(context)

        assertTrue("Expected countries to be loaded from assets", countries.isNotEmpty())
        assertTrue("Expected the United States to be present", countries.any { it.code == "US" })
        assertEquals("Countries should be sorted by name", countries.sortedBy { it.name }, countries)
    }

    @Test
    fun unreadableAssetReturnsEmptyList() {
        val context = mockk<Context>(relaxed = true) {
            every { assets } returns mockk(relaxed = true) {
                every { open(any<String>()) } throws IOException("missing asset")
            }
        }

        assertTrue(CountryDataLoader.loadCountriesFromAssets(context).isEmpty())
    }

    @Test
    fun malformedJsonReturnsEmptyList() {
        val stream: InputStream = "this is not json".byteInputStream()
        val context = mockk<Context>(relaxed = true) {
            every { assets } returns mockk(relaxed = true) {
                every { open(any<String>()) } returns stream
            }
        }

        assertTrue(CountryDataLoader.loadCountriesFromAssets(context).isEmpty())
    }
}
