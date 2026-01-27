package com.tecruz.countrytracker.features.countrylist.domain

import app.cash.turbine.test
import com.tecruz.countrytracker.core.domain.repository.CountryRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCountryStatisticsUseCaseTest {

    private lateinit var repository: CountryRepository
    private lateinit var useCase: GetCountryStatisticsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetCountryStatisticsUseCase(repository)
    }

    @Test
    fun `invoke should calculate correct statistics`() = runTest {
        every { repository.getVisitedCount() } returns flowOf(25)
        every { repository.getTotalCount() } returns flowOf(100)

        useCase().test {
            val result = awaitItem()
            assertEquals(25, result.visitedCount)
            assertEquals(100, result.totalCount)
            assertEquals(25, result.percentage)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle zero total countries`() = runTest {
        every { repository.getVisitedCount() } returns flowOf(0)
        every { repository.getTotalCount() } returns flowOf(0)

        useCase().test {
            val result = awaitItem()
            assertEquals(0, result.visitedCount)
            assertEquals(0, result.totalCount)
            assertEquals(0, result.percentage) // Should not divide by zero
            awaitComplete()
        }
    }

    @Test
    fun `invoke should calculate percentage with rounding down`() = runTest {
        every { repository.getVisitedCount() } returns flowOf(1)
        every { repository.getTotalCount() } returns flowOf(3)

        useCase().test {
            val result = awaitItem()
            assertEquals(33, result.percentage) // 1/3 = 33% (integer division)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return 100 percent when all visited`() = runTest {
        every { repository.getVisitedCount() } returns flowOf(50)
        every { repository.getTotalCount() } returns flowOf(50)

        useCase().test {
            val result = awaitItem()
            assertEquals(100, result.percentage)
            awaitComplete()
        }
    }
}
