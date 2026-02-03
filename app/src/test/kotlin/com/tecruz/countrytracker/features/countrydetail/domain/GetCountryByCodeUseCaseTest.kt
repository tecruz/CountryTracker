package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.features.countrydetail.domain.model.CountryDetail
import com.tecruz.countrytracker.features.countrydetail.domain.repository.CountryDetailRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GetCountryByCodeUseCaseTest {

    private lateinit var repository: CountryDetailRepository
    private lateinit var useCase: GetCountryByCodeUseCase

    private val testCountry = CountryDetail(
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
        coEvery { repository.getCountryByCode("US") } returns testCountry

        val result = useCase("US")

        assertEquals(testCountry, result)
        coVerify { repository.getCountryByCode("US") }
    }

    @Test
    fun `invoke should return null when country not found`() = runTest {
        coEvery { repository.getCountryByCode("XX") } returns null

        val result = useCase("XX")

        assertNull(result)
        coVerify { repository.getCountryByCode("XX") }
    }

    @Test
    fun `invoke should pass country code to repository`() = runTest {
        val expected = testCountry.copy(code = "FR")
        coEvery { repository.getCountryByCode("FR") } returns expected

        val result = useCase("FR")

        assertEquals(expected, result)
        coVerify { repository.getCountryByCode("FR") }
    }

    @Test
    fun `invoke should handle empty country code`() = runTest {
        coEvery { repository.getCountryByCode("") } returns null

        val result = useCase("")

        assertNull(result)
        coVerify { repository.getCountryByCode("") }
    }
}
