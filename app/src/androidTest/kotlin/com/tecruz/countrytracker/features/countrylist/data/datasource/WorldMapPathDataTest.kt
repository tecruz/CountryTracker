package com.tecruz.countrytracker.features.countrylist.data.datasource

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 * Instrumented tests for [WorldMapPathData].
 * Requires a real Android context to read assets/world_map_paths.json.
 */
@RunWith(AndroidJUnit4::class)
class WorldMapPathDataTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setup() {
        WorldMapPathData.reset()
    }

    @After
    fun tearDown() {
        WorldMapPathData.reset()
    }

    @Test
    fun isLoaded_returnsFalse_beforeLoading() {
        assertFalse(WorldMapPathData.isLoaded)
    }

    @Test
    fun loadCountryPaths_loadsData_andSetsIsLoadedTrue() {
        val count = WorldMapPathData.loadCountryPaths(context)

        assertTrue(WorldMapPathData.isLoaded)
        assertTrue("Should load at least one country path", count > 0)
    }

    @Test
    fun countryPaths_returnsNonEmptyMap_afterLoading() {
        WorldMapPathData.loadCountryPaths(context)

        val paths = WorldMapPathData.countryPaths
        assertTrue("Country paths map should not be empty", paths.isNotEmpty())
    }

    @Test
    fun countryPaths_containsExpectedCountryCodes() {
        WorldMapPathData.loadCountryPaths(context)

        val paths = WorldMapPathData.countryPaths
        // Spot-check a few well-known ISO country codes
        assertTrue("Should contain US", paths.containsKey("US"))
        assertTrue("Should contain BR", paths.containsKey("BR"))
        assertTrue("Should contain JP", paths.containsKey("JP"))
        assertTrue("Should contain FR", paths.containsKey("FR"))
    }

    @Test
    fun countryPaths_valuesAreNonEmptyStrings() {
        WorldMapPathData.loadCountryPaths(context)

        WorldMapPathData.countryPaths.forEach { (code, pathData) ->
            assertTrue("Path data for $code should not be blank", pathData.isNotBlank())
        }
    }

    @Test
    fun loadCountryPaths_isIdempotent_returnsSameCount() {
        val firstCount = WorldMapPathData.loadCountryPaths(context)
        val secondCount = WorldMapPathData.loadCountryPaths(context)

        assertEquals("Idempotent calls should return the same count", firstCount, secondCount)
        assertTrue(WorldMapPathData.isLoaded)
    }

    @Test(expected = IllegalStateException::class)
    fun countryPaths_throwsError_whenAccessedBeforeLoading() {
        // Accessing countryPaths before calling loadCountryPaths should throw
        WorldMapPathData.countryPaths
    }

    @Test
    fun loadCountryPaths_isThreadSafe_concurrentCalls() {
        val threadCount = 10
        val latch = CountDownLatch(threadCount)
        val results = AtomicInteger(0)
        val errors = AtomicInteger(0)

        // Launch multiple threads that all try to load concurrently
        repeat(threadCount) {
            thread {
                try {
                    val count = WorldMapPathData.loadCountryPaths(context)
                    if (count > 0) results.incrementAndGet()
                } catch (_: Exception) {
                    errors.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()

        assertEquals("No errors should occur during concurrent loading", 0, errors.get())
        assertEquals("All threads should get a valid count", threadCount, results.get())
        assertTrue(WorldMapPathData.isLoaded)
    }

    @Test
    fun viewBoxDimensions_arePositive() {
        assertTrue("VIEW_BOX_WIDTH should be positive", WorldMapPathData.VIEW_BOX_WIDTH > 0f)
        assertTrue("VIEW_BOX_HEIGHT should be positive", WorldMapPathData.VIEW_BOX_HEIGHT > 0f)
    }
}
