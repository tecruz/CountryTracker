package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.core.domain.repository.CountryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GetCountryByCodeUseCaseTest {

    private lateinit var repository: CountryRepository
    private lateinit var useCase: GetCountryByCodeUseCase

    private val testCountry = Country(
        code = "US",
        name = "United States",
        region = "North America",
        visited = false,
        visitedDate = null,
        notes = "",
        rating = 0,
        flagEmoji = "🇺🇸",
    )

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetCountryByCodeUseCase(repository)
    }

    @Test
    fun `invoke should delegate to repository getCountryByCode and return result`() = runTest {
        every { repository.getCountryByCodeFlow("US") } returns flowOf(testCountry)

        val result = useCase("US").first()

        assertEquals(testCountry, result)
        verify { repository.getCountryByCodeFlow("US") }
    }

    @Test
    fun `invoke should return null when country not found`() = runTest {
        every { repository.getCountryByCodeFlow("XX") } returns flowOf(null)

        val result = useCase("XX").first()

        assertNull(result)
        verify { repository.getCountryByCodeFlow("XX") }
    }

    @Test
    fun `invoke should pass country code to repository`() = runTest {
        val expected = testCountry.copy(code = "FR")
        every { repository.getCountryByCodeFlow("FR") } returns flowOf(expected)

        val result = useCase("FR").first()

        assertEquals(expected, result)
        verify { repository.getCountryByCodeFlow("FR") }
    }

    @Test
    fun `invoke should handle empty country code`() = runTest {
        every { repository.getCountryByCodeFlow("") } returns flowOf(null)

        val result = useCase("").first()

        assertNull(result)
        verify { repository.getCountryByCodeFlow("") }
    }
}
