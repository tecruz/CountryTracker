package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.core.domain.repository.CountryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class UpdateCountryRatingUseCaseTest {

    private lateinit var repository: CountryRepository
    private lateinit var useCase: UpdateCountryRatingUseCase

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
        useCase = UpdateCountryRatingUseCase(repository)
    }

    @Test
    fun `invoke should update country rating via repository`() = runTest {
        val newRating = 4
        coEvery { repository.updateCountry(any()) } returns Unit

        useCase(testCountry, newRating)

        coVerify { repository.updateCountry(testCountry.copy(rating = newRating)) }
    }

    @Test
    fun `invoke should accept minimum rating`() = runTest {
        coEvery { repository.updateCountry(any()) } returns Unit
        useCase(testCountry, Country.MIN_RATING)

        coVerify { repository.updateCountry(testCountry.copy(rating = Country.MIN_RATING)) }
    }

    @Test
    fun `invoke should accept mid-range rating values`() = runTest {
        coEvery { repository.updateCountry(any()) } returns Unit
        useCase(testCountry, 2)
        coVerify { repository.updateCountry(testCountry.copy(rating = 2)) }
    }

    @Test
    fun `invoke should accept maximum rating`() = runTest {
        coEvery { repository.updateCountry(any()) } returns Unit
        useCase(testCountry, Country.MAX_RATING)

        coVerify { repository.updateCountry(testCountry.copy(rating = Country.MAX_RATING)) }
    }

    @Test
    fun `invoke should reject negative rating`() = runTest {
        assertThrows(IllegalArgumentException::class.java) {
            kotlinx.coroutines.runBlocking {
                useCase(testCountry, -1)
            }
        }

        coVerify(exactly = 0) { repository.updateCountry(any()) }
    }

    @Test
    fun `invoke should reject rating above maximum`() = runTest {
        assertThrows(IllegalArgumentException::class.java) {
            kotlinx.coroutines.runBlocking {
                useCase(testCountry, Country.MAX_RATING + 1)
            }
        }

        coVerify(exactly = 0) { repository.updateCountry(any()) }
    }

    @Test
    fun `invoke should accept zero rating`() = runTest {
        coEvery { repository.updateCountry(any()) } returns Unit
        useCase(testCountry, 0)

        coVerify { repository.updateCountry(testCountry.copy(rating = 0)) }
    }

    @Test
    fun `invoke should create copy with updated rating preserving other fields`() = runTest {
        val newRating = 5
        coEvery { repository.updateCountry(any()) } returns Unit

        useCase(testCountry, newRating)

        coVerify {
            repository.updateCountry(
                match {
                    it.rating == newRating &&
                        it.code == testCountry.code &&
                        it.name == testCountry.name &&
                        it.region == testCountry.region &&
                        it.visited == testCountry.visited &&
                        it.visitedDate == testCountry.visitedDate &&
                        it.notes == testCountry.notes &&
                        it.flagEmoji == testCountry.flagEmoji
                },
            )
        }
    }

    @Test
    fun `invoke should propagate repository exception`() = runTest {
        coEvery { repository.updateCountry(any()) } throws RuntimeException("Database error")

        var exception: Throwable? = null
        try {
            useCase(testCountry, 4)
        } catch (e: RuntimeException) {
            exception = e
        }

        assertNotNull(exception)
        assertEquals("Database error", exception!!.message)
    }
}
