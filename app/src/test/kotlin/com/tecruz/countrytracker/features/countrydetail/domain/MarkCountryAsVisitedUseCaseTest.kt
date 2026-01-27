package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.core.domain.repository.CountryRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MarkCountryAsVisitedUseCaseTest {

    private lateinit var repository: CountryRepository
    private lateinit var useCase: MarkCountryAsVisitedUseCase

    private val testCountry = Country(
        code = "US",
        name = "United States",
        region = "North America",
        visited = false,
        visitedDate = null,
        notes = "",
        rating = 0,
        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8"
    )

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        useCase = MarkCountryAsVisitedUseCase(repository)
    }

    @Test
    fun `invoke should call repository markAsVisited with correct parameters`() = runTest {
        val date = 1704067200000L
        val notes = "Great trip!"
        val rating = 5

        useCase(testCountry, date, notes, rating)

        coVerify {
            repository.markAsVisited(testCountry, date, notes, rating)
        }
    }

    @Test
    fun `invoke should use default values for notes and rating`() = runTest {
        val date = 1704067200000L

        useCase(testCountry, date)

        coVerify {
            repository.markAsVisited(testCountry, date, "", 0)
        }
    }

    @Test
    fun `invoke should pass empty notes when provided`() = runTest {
        val date = 1704067200000L

        useCase(testCountry, date, "", 3)

        coVerify {
            repository.markAsVisited(testCountry, date, "", 3)
        }
    }

    @Test
    fun `invoke should handle long notes`() = runTest {
        val date = 1704067200000L
        val longNotes = "A".repeat(500)

        useCase(testCountry, date, longNotes, 4)

        coVerify {
            repository.markAsVisited(testCountry, date, longNotes, 4)
        }
    }

    @Test
    fun `invoke should handle zero rating`() = runTest {
        val date = 1704067200000L

        useCase(testCountry, date, "Some notes", 0)

        coVerify {
            repository.markAsVisited(testCountry, date, "Some notes", 0)
        }
    }

    @Test
    fun `invoke should handle maximum rating`() = runTest {
        val date = 1704067200000L

        useCase(testCountry, date, "Perfect!", 5)

        coVerify {
            repository.markAsVisited(testCountry, date, "Perfect!", 5)
        }
    }
}
