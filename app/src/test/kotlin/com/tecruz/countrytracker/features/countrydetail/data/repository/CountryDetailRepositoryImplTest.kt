package com.tecruz.countrytracker.features.countrydetail.data.repository

import com.tecruz.countrytracker.core.data.database.CountryDao
import com.tecruz.countrytracker.core.data.database.CountryEntity
import com.tecruz.countrytracker.features.countrydetail.domain.model.CountryDetail
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CountryDetailRepositoryImplTest {

    private lateinit var dao: CountryDao
    private lateinit var repository: CountryDetailRepositoryImpl

    private val testEntities = listOf(
        CountryEntity("US", "United States", "North America", false, null, "", 0, "\uD83C\uDDFA\uD83C\uDDF8"),
        CountryEntity("FR", "France", "Europe", true, 1704067200000L, "Great trip!", 5, "\uD83C\uDDEB\uD83C\uDDF7"),
    )

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = CountryDetailRepositoryImpl(dao)
    }

    @Test
    fun `getCountryByCode should return correct country`() = runTest {
        coEvery { dao.getCountryByCode("US") } returns testEntities.first()

        val result = repository.getCountryByCode("US")
        assertEquals("United States", result?.name)
        assertEquals("North America", result?.region)
    }

    @Test
    fun `getCountryByCode should return null for unknown code`() = runTest {
        coEvery { dao.getCountryByCode("XX") } returns null

        val result = repository.getCountryByCode("XX")
        assertNull(result)
    }

    @Test
    fun `updateCountry should call dao update`() = runTest {
        val country = testEntities.first().let {
            CountryDetail(
                it.code,
                it.name,
                it.region,
                it.visited,
                it.visitedDate,
                it.notes,
                it.rating,
                it.flagEmoji,
            )
        }

        repository.updateCountry(country)

        coVerify { dao.updateCountry(any()) }
    }

    @Test
    fun `markAsVisited should update country with visit info`() = runTest {
        val country = CountryDetail(
            "US",
            "United States",
            "North America",
            false,
            null,
            "",
            0,
            "\uD83C\uDDFA\uD83C\uDDF8",
        )
        val date = 1704067200000L

        repository.markAsVisited(country, date, "Great!", 5)

        coVerify {
            dao.updateCountry(
                match {
                    it.visited && it.visitedDate == date && it.notes == "Great!" && it.rating == 5
                },
            )
        }
    }

    @Test
    fun `markAsUnvisited should reset country visit info`() = runTest {
        val country = CountryDetail(
            "FR",
            "France",
            "Europe",
            true,
            1704067200000L,
            "Great trip!",
            5,
            "\uD83C\uDDEB\uD83C\uDDF7",
        )

        repository.markAsUnvisited(country)

        coVerify {
            dao.updateCountry(
                match {
                    !it.visited && it.visitedDate == null && it.notes.isEmpty() && it.rating == 0
                },
            )
        }
    }
}
