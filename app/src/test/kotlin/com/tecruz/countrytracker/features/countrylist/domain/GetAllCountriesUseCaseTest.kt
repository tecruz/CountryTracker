package com.tecruz.countrytracker.features.countrylist.domain

import app.cash.turbine.test
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem
import com.tecruz.countrytracker.features.countrylist.domain.repository.CountryListRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAllCountriesUseCaseTest {

    private lateinit var repository: CountryListRepository
    private lateinit var useCase: GetAllCountriesUseCase

    private val testCountries = listOf(
        CountryListItem("US", "United States", "North America", false, "\uD83C\uDDFA\uD83C\uDDF8"),
        CountryListItem("FR", "France", "Europe", true, "\uD83C\uDDEB\uD83C\uDDF7"),
    )

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetAllCountriesUseCase(repository)
    }

    @Test
    fun `invoke should return countries from repository`() = runTest {
        every { repository.getAllCountries() } returns flowOf(testCountries)

        useCase().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("United States", result[0].name)
            assertEquals("France", result[1].name)
            awaitComplete()
        }

        verify { repository.getAllCountries() }
    }

    @Test
    fun `invoke should return empty list when no countries`() = runTest {
        every { repository.getAllCountries() } returns flowOf(emptyList())

        useCase().test {
            val result = awaitItem()
            assertEquals(0, result.size)
            awaitComplete()
        }
    }
}
