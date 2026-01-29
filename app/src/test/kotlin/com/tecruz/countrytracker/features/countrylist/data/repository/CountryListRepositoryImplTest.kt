package com.tecruz.countrytracker.features.countrylist.data.repository

import app.cash.turbine.test
import com.tecruz.countrytracker.core.data.database.CountryDao
import com.tecruz.countrytracker.core.data.database.CountryEntity
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CountryListRepositoryImplTest {

    private lateinit var dao: CountryDao
    private lateinit var repository: CountryListRepositoryImpl

    private val testEntities = listOf(
        CountryEntity("US", "United States", "North America", false, null, "", 0, "\uD83C\uDDFA\uD83C\uDDF8"),
        CountryEntity("FR", "France", "Europe", true, 1704067200000L, "Great trip!", 5, "\uD83C\uDDEB\uD83C\uDDF7"),
    )

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = CountryListRepositoryImpl(dao)
    }

    @Test
    fun `getAllCountries should return mapped countries`() = runTest {
        every { dao.getAllCountries() } returns flowOf(testEntities)

        repository.getAllCountries().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("United States", result[0].name)
            assertEquals("US", result[0].code)
            assertEquals("France", result[1].name)
            assertTrue(result[1].visited)
            awaitComplete()
        }
    }

    @Test
    fun `getCountriesByRegion should filter by region`() = runTest {
        val europeEntities = testEntities.filter { it.region == "Europe" }
        every { dao.getCountriesByRegion("Europe") } returns flowOf(europeEntities)

        repository.getCountriesByRegion("Europe").test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Europe", result.first().region)
            awaitComplete()
        }
    }

    @Test
    fun `getVisitedCount should return count`() = runTest {
        every { dao.getVisitedCount() } returns flowOf(10)

        repository.getVisitedCount().test {
            assertEquals(10, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getTotalCount should return count`() = runTest {
        every { dao.getTotalCount() } returns flowOf(100)

        repository.getTotalCount().test {
            assertEquals(100, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getAllRegions should return unique regions`() = runTest {
        every { dao.getAllRegions() } returns flowOf(listOf("Europe", "Asia", "North America"))

        repository.getAllRegions().test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertTrue(result.contains("Europe"))
            awaitComplete()
        }
    }
}
