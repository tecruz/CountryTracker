package com.tecruz.countrytracker.features.countrylist.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.tecruz.countrytracker.core.data.database.CountryDatabase
import com.tecruz.countrytracker.core.data.database.CountryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class CountryListRepositoryIntegrationTest {

    private lateinit var database: CountryDatabase
    private lateinit var dao: com.tecruz.countrytracker.core.data.database.CountryDao
    private lateinit var repository: CountryListRepositoryImpl

    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CountryDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = database.countryDao()
        repository = CountryListRepositoryImpl(dao)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        database.close()
    }

    @Test
    fun getAllCountries_should_return_mapped_CountryListItem_with_all_fields() = runTest {
        dao.insertCountries(listOf(testEntity1, testEntity2, testEntity3))

        repository.getAllCountries().test {
            val result = awaitItem()
            assertEquals(3, result.size)

            val usResult = result.find { it.code == "US" }
            assertEquals("United States", usResult?.name)
            assertEquals("North America", usResult?.region)
            assertFalse(usResult?.visited ?: true)
            assertEquals("\uD83C\uDDFA\uD83C\uDDF8", usResult?.flagEmoji)

            val frResult = result.find { it.code == "FR" }
            assertEquals("France", frResult?.name)
            assertEquals("Europe", frResult?.region)
            assertTrue(frResult?.visited ?: false)
            assertEquals("\uD83C\uDDEB\uD83C\uDDF7", frResult?.flagEmoji)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllCountries_should_map_entity_with_empty_flagEmoji_correctly() = runTest {
        val entityWithEmptyFlag = testEntity1.copy(flagEmoji = "")
        dao.insertCountry(entityWithEmptyFlag)

        repository.getAllCountries().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("", result[0].flagEmoji)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllCountries_should_preserve_unicode_flagEmoji() = runTest {
        val entityWithUnicodeFlag = CountryEntity(
            code = "ES",
            name = "Spain",
            region = "Europe",
            visited = false,
            visitedDate = null,
            notes = "",
            rating = 0,
            flagEmoji = "\uD83C\uDDEA\uD83C\uDDF8",
        )
        dao.insertCountry(entityWithUnicodeFlag)

        repository.getAllCountries().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("\uD83C\uDDEA\uD83C\uDDF8", result[0].flagEmoji)
            assertEquals("ES", result[0].code)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getCountriesByRegion_should_filter_and_map_entities() = runTest {
        dao.insertCountries(listOf(testEntity1, testEntity2, testEntity3))

        repository.getCountriesByRegion("Europe").test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("France", result[0].name)
            assertEquals("Europe", result[0].region)
            assertEquals("FR", result[0].code)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getVisitedCount_should_return_correct_count() = runTest {
        dao.insertCountries(listOf(testEntity1, testEntity2, testEntity3))

        repository.getVisitedCount().test {
            val result = awaitItem()
            assertEquals(2, result)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getTotalCount_should_return_correct_total() = runTest {
        dao.insertCountries(listOf(testEntity1, testEntity2, testEntity3))

        repository.getTotalCount().test {
            val result = awaitItem()
            assertEquals(3, result)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllRegions_should_return_distinct_regions() = runTest {
        dao.insertCountries(listOf(testEntity1, testEntity2, testEntity3))

        repository.getAllRegions().test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertTrue(result.contains("Asia"))
            assertTrue(result.contains("Europe"))
            assertTrue(result.contains("North America"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllCountries_should_handle_entity_with_non_default_excluded_fields_correctly() = runTest {
        val entityWithExtraFields = CountryEntity(
            code = "DE",
            name = "Germany",
            region = "Europe",
            visited = true,
            visitedDate = 1704067200000L,
            notes = "Long trip with lots of memories",
            rating = 5,
            flagEmoji = "\uD83C\uDDE9\uD83C\uDDEA",
        )
        dao.insertCountry(entityWithExtraFields)

        repository.getAllCountries().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            val deResult = result[0]
            assertEquals("Germany", deResult.name)
            assertEquals("Europe", deResult.region)
            assertEquals("DE", deResult.code)
            assertTrue(deResult.visited)
            assertEquals("\uD83C\uDDE9\uD83C\uDDEA", deResult.flagEmoji)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAll_methods_should_return_empty_on_empty_database() = runTest {
        repository.getAllCountries().test {
            val result = awaitItem()
            assertEquals(0, result.size)
            cancelAndIgnoreRemainingEvents()
        }

        repository.getVisitedCount().test {
            val result = awaitItem()
            assertEquals(0, result)
            cancelAndIgnoreRemainingEvents()
        }

        repository.getTotalCount().test {
            val result = awaitItem()
            assertEquals(0, result)
            cancelAndIgnoreRemainingEvents()
        }

        repository.getAllRegions().test {
            val result = awaitItem()
            assertEquals(0, result.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    companion object {
        private val testEntity1 = CountryEntity(
            code = "US",
            name = "United States",
            region = "North America",
            visited = false,
            flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8",
        )
        private val testEntity2 = CountryEntity(
            code = "FR",
            name = "France",
            region = "Europe",
            visited = true,
            visitedDate = 1704067200000L,
            flagEmoji = "\uD83C\uDDEB\uD83C\uDDF7",
        )
        private val testEntity3 = CountryEntity(
            code = "JP",
            name = "Japan",
            region = "Asia",
            visited = true,
            visitedDate = 1703980800000L,
            flagEmoji = "\uD83C\uDDEF\uD83C\uDDF5",
        )
    }
}
