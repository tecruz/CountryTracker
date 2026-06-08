package com.tecruz.countrytracker.features.countrylist.domain

import app.cash.turbine.test
import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.core.domain.repository.CountryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAllCountriesUseCaseTest {

    private lateinit var repository: CountryRepository
    private lateinit var useCase: GetAllCountriesUseCase

    private val testCountries = listOf(
        Country("US", "United States", "North America", false, flagEmoji = "🇺🇸"),
        Country("FR", "France", "Europe", true, flagEmoji = "🇫🇷"),
    )

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetAllCountriesUseCase(repository)
    }

    @Test
    fun `invoke should return countries from repository using filters`() = runTest {
        val filteredCountries = listOf(testCountries[1])
        every { repository.getFilteredCountries("France", "Europe", true) } returns flowOf(filteredCountries)

        useCase("France", "Europe", true).test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("France", result[0].name)
            awaitComplete()
        }

        verify { repository.getFilteredCountries("France", "Europe", true) }
    }

    @Test
    fun `invoke should return empty list when no countries`() = runTest {
        every { repository.getFilteredCountries(any(), any(), any()) } returns flowOf(emptyList())

        useCase().test {
            val result = awaitItem()
            assertEquals(0, result.size)
            awaitComplete()
        }

        verify { repository.getFilteredCountries("", "All", false) }
    }
}
