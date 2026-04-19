package com.tecruz.countrytracker.features.countrylist.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class CountryStatisticsTest {

    @Test
    fun `creates with all fields`() {
        val stats = CountryStatistics(
            visitedCount = 25,
            totalCount = 100,
            percentage = 25,
        )

        assertEquals(25, stats.visitedCount)
        assertEquals(100, stats.totalCount)
        assertEquals(25, stats.percentage)
    }

    @Test
    fun `handles zero total`() {
        val stats = CountryStatistics(0, 0, 0)
        assertEquals(0, stats.percentage)
    }

    @Test
    fun `handles 100 percent`() {
        val stats = CountryStatistics(50, 50, 100)
        assertEquals(100, stats.percentage)
    }

    @Test
    fun `equals compares all fields`() {
        val s1 = CountryStatistics(10, 50, 20)
        val s2 = CountryStatistics(10, 50, 20)
        assertEquals(s1, s2)
    }
}
