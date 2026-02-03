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

class UpdateCountryNotesUseCaseTest {

    private lateinit var repository: CountryDetailRepository
    private lateinit var useCase: UpdateCountryNotesUseCase

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
        useCase = UpdateCountryNotesUseCase(repository)
    }

    @Test
    fun `invoke should update country notes via repository`() = runTest {
        val newNotes = "Great trip!"
        coEvery { repository.updateCountry(any()) } returns Unit

        useCase(testCountry, newNotes)

        coVerify { repository.updateCountry(testCountry.copy(notes = newNotes)) }
    }

    @Test
    fun `invoke should accept empty notes`() = runTest {
        coEvery { repository.updateCountry(any()) } returns Unit
        useCase(testCountry, "")

        coVerify { repository.updateCountry(testCountry.copy(notes = "")) }
    }

    @Test
    fun `invoke should accept notes at max length boundary`() = runTest {
        val maxNotes = "A".repeat(CountryDetail.MAX_NOTES_LENGTH)
        coEvery { repository.updateCountry(any()) } returns Unit

        useCase(testCountry, maxNotes)

        coVerify { repository.updateCountry(testCountry.copy(notes = maxNotes)) }
    }

    @Test
    fun `invoke should reject notes exceeding max length`() {
        val tooLongNotes = "A".repeat(CountryDetail.MAX_NOTES_LENGTH + 1)

        assertThrows(IllegalArgumentException::class.java) {
            kotlinx.coroutines.test.runTest {
                useCase(testCountry, tooLongNotes)
            }
        }

        coVerify(exactly = 0) { repository.updateCountry(any()) }
    }

    @Test
    fun `invoke should create copy with updated notes preserving other fields`() = runTest {
        val newNotes = "Updated notes"
        coEvery { repository.updateCountry(any()) } returns Unit

        useCase(testCountry, newNotes)

        coVerify {
            repository.updateCountry(
                match {
                    it.notes == newNotes &&
                        it.code == testCountry.code &&
                        it.name == testCountry.name &&
                        it.region == testCountry.region &&
                        it.visited == testCountry.visited &&
                        it.visitedDate == testCountry.visitedDate &&
                        it.rating == testCountry.rating &&
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
            useCase(testCountry, "New notes")
        } catch (e: RuntimeException) {
            exception = e
        }

        assertNotNull(exception)
        assertEquals("Database error", exception!!.message)
    }
}
