package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.features.countrydetail.domain.model.CountryDetail
import com.tecruz.countrytracker.features.countrydetail.domain.repository.CountryDetailRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class MarkCountryAsUnvisitedUseCaseTest {

    private lateinit var repository: CountryDetailRepository
    private lateinit var useCase: MarkCountryAsUnvisitedUseCase

    companion object {
        private const val TEST_VISITED_DATE = 1704067200000L
    }

    private val testCountry = CountryDetail(
        code = "FR",
        name = "France",
        region = "Europe",
        visited = true,
        visitedDate = TEST_VISITED_DATE,
        notes = "Great trip!",
        rating = 5,
        flagEmoji = "🇫🇷",
    )

    @Before
    fun setup() {
        repository = mockk()
        useCase = MarkCountryAsUnvisitedUseCase(repository)
    }

    @Test
    fun `invoke should delegate to repository markAsUnvisited`() = runTest {
        coEvery { repository.markAsUnvisited(any()) } returns Unit
        useCase(testCountry)

        coVerify { repository.markAsUnvisited(testCountry) }
    }

    @Test
    fun `invoke should propagate repository exception`() = runTest {
        coEvery { repository.markAsUnvisited(any()) } throws RuntimeException("Database error")

        var exception: Throwable? = null
        try {
            useCase(testCountry)
        } catch (e: RuntimeException) {
            exception = e
        }

        assertNotNull(exception)
        assertEquals("Database error", exception!!.message)
    }
}
