package com.tecruz.countrytracker.features.countrydetail.domain

import com.tecruz.countrytracker.features.countrydetail.domain.model.CountryDetail
import com.tecruz.countrytracker.features.countrydetail.domain.repository.CountryDetailRepository
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

    private lateinit var repository: CountryDetailRepository
    private lateinit var useCase: UpdateCountryRatingUseCase

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
        useCase(testCountry, CountryDetail.MIN_RATING)

        coVerify { repository.updateCountry(testCountry.copy(rating = CountryDetail.MIN_RATING)) }
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
        useCase(testCountry, CountryDetail.MAX_RATING)

        coVerify { repository.updateCountry(testCountry.copy(rating = CountryDetail.MAX_RATING)) }
    }

    @Test
    fun `invoke should reject negative rating`() {
        assertThrows(IllegalArgumentException::class.java) {
            kotlinx.coroutines.test.runTest {
                useCase(testCountry, -1)
            }
        }

        coVerify(exactly = 0) { repository.updateCountry(any()) }
    }

    @Test
    fun `invoke should reject rating above maximum`() {
        assertThrows(IllegalArgumentException::class.java) {
            kotlinx.coroutines.test.runTest {
                useCase(testCountry, CountryDetail.MAX_RATING + 1)
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
