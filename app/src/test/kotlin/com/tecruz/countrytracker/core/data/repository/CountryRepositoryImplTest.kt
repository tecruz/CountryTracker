package com.tecruz.countrytracker.core.data.repository

import app.cash.turbine.test
import com.tecruz.countrytracker.core.data.database.CountryDao
import com.tecruz.countrytracker.core.data.database.CountryEntity
import com.tecruz.countrytracker.core.data.mapper.toEntity
import com.tecruz.countrytracker.core.domain.model.Country
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CountryRepositoryImplTest {

    private lateinit var dao: CountryDao
    private lateinit var repository: CountryRepositoryImpl

    private val testEntities = listOf(
        CountryEntity("US", "United States", "North America", false, null, "", 0, "🇺🇸"),
        CountryEntity("FR", "France", "Europe", true, 1704067200000L, "Great trip!", 5, "🇫🇷"),
    )

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = CountryRepositoryImpl(dao)
    }

    @Test
    fun `getFilteredCountries should return mapped countries from dao`() = runTest {
        every { dao.getFilteredCountries("France", "Europe", true) } returns flowOf(listOf(testEntities[1]))

        repository.getFilteredCountries("France", "Europe", true).test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("France", result[0].name)
            assertEquals("FR", result[0].code)
            assertTrue(result[0].visited)
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
    fun `getAllRegions should return regions`() = runTest {
        every { dao.getAllRegions() } returns flowOf(listOf("Europe", "Asia", "North America"))

        repository.getAllRegions().test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertTrue(result.contains("Europe"))
            awaitComplete()
        }
    }

    @Test
    fun `getVisitedCountryCodes should return codes`() = runTest {
        every { dao.getVisitedCountryCodes() } returns flowOf(listOf("FR", "BR"))

        repository.getVisitedCountryCodes().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertTrue(result.contains("FR"))
            assertTrue(result.contains("BR"))
            awaitComplete()
        }
    }

    @Test
    fun `getCountryByCode should return mapped country when found`() = runTest {
        coEvery { dao.getCountryByCode("FR") } returns testEntities[1]

        val result = repository.getCountryByCode("FR")

        assertEquals("France", result?.name)
        assertEquals("FR", result?.code)
    }

    @Test
    fun `getCountryByCode should return null when not found`() = runTest {
        coEvery { dao.getCountryByCode("XX") } returns null

        val result = repository.getCountryByCode("XX")

        assertNull(result)
    }

    @Test
    fun `updateCountry should call dao`() = runTest {
        val country = Country("PT", "Portugal", "Europe", false, flagEmoji = "🇵🇹")

        repository.updateCountry(country)

        coVerify { dao.updateCountry(country.toEntity()) }
    }

    @Test
    fun `markAsVisited should update country with visited data`() = runTest {
        val country = Country("PT", "Portugal", "Europe", false, flagEmoji = "🇵🇹")
        val date = 123456789L
        val notes = "Nice place"
        val rating = 4

        repository.markAsVisited(country, date, notes, rating)

        coVerify {
            dao.updateCountry(
                match {
                    it.code == "PT" && it.visited && it.visitedDate == date && it.notes == notes && it.rating == rating
                },
            )
        }
    }

    @Test
    fun `markAsUnvisited should reset visited fields`() = runTest {
        val country = Country("FR", "France", "Europe", true, 12345L, "Notes", 5, "🇫🇷")

        repository.markAsUnvisited(country)

        coVerify {
            dao.updateCountry(
                match {
                    it.code == "FR" && !it.visited && it.visitedDate == null && it.notes == "" && it.rating == 0
                },
            )
        }
    }
}
