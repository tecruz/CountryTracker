package com.tecruz.countrytracker.core.data.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class CountryDaoTest {

    private lateinit var database: CountryDatabase
    private lateinit var dao: CountryDao

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
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        database.close()
    }

    @Test
    fun getAllCountries_should_return_empty_list_when_database_is_empty() = runTest {
        dao.getAllCountries().test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllCountries_should_return_all_entities_ordered_by_name_ASC() = runTest {
        dao.insertCountries(listOf(testCountry1, testCountry2, testCountry3))

        val result = dao.getAllCountries().first()
        assertEquals(3, result.size)
        assertEquals("France", result[0].name)
        assertEquals("Japan", result[1].name)
        assertEquals("United States", result[2].name)
    }

    @Test
    fun getVisitedCountries_should_return_only_visited_entities_ordered_by_visitedDate_DESC() = runTest {
        dao.insertCountries(listOf(testCountry1, testCountry2, testCountry3))

        dao.getVisitedCountries().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("France", result[0].name)
            assertEquals("Japan", result[1].name)
            assertTrue(result.all { it.visited })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getVisitedCountries_should_handle_entities_with_equal_visitedDate() = runTest {
        val testCountrySameDate = CountryEntity(
            code = "DE",
            name = "Germany",
            region = "Europe",
            visited = true,
            visitedDate = 1704067200000L, // Same date as FR
            notes = "",
            rating = 3,
            flagEmoji = "\uD83C\uDDE9\uD83C\uDDEA",
        )
        dao.insertCountries(listOf(testCountry2, testCountrySameDate))

        dao.getVisitedCountries().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertTrue(result.any { it.code == "FR" })
            assertTrue(result.any { it.code == "DE" })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getUnvisitedCountries_should_return_only_unvisited_entities_ordered_by_name_ASC() = runTest {
        dao.insertCountries(listOf(testCountry1, testCountry2, testCountry3))

        dao.getUnvisitedCountries().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("United States", result[0].name)
            assertTrue(!result[0].visited)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getCountriesByRegion_should_filter_by_region_and_order_by_name_ASC() = runTest {
        dao.insertCountries(listOf(testCountry1, testCountry2, testCountry3))

        dao.getCountriesByRegion("Europe").test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("France", result[0].name)
            assertEquals("Europe", result[0].region)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getCountriesByRegion_should_return_empty_list_for_non_existent_region() = runTest {
        dao.insertCountries(listOf(testCountry1, testCountry2, testCountry3))

        dao.getCountriesByRegion("Antarctica").test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getCountryByCode_should_return_matching_entity() = runTest {
        dao.insertCountry(testCountry1)

        val result = dao.getCountryByCode("US")
        assertEquals("United States", result?.name)
        assertEquals("North America", result?.region)
        assertEquals("US", result?.code)
    }

    @Test
    fun getCountryByCode_should_return_null_for_unknown_code() = runTest {
        dao.insertCountry(testCountry1)

        val result = dao.getCountryByCode("XX")
        assertEquals(null, result)
    }

    @Test
    fun getVisitedCount_should_return_count_of_visited_entities() = runTest {
        dao.insertCountries(listOf(testCountry1, testCountry2, testCountry3))

        dao.getVisitedCount().test {
            val result = awaitItem()
            assertEquals(2, result)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getTotalCount_should_return_total_entity_count() = runTest {
        dao.insertCountries(listOf(testCountry1, testCountry2, testCountry3))

        dao.getTotalCount().test {
            val result = awaitItem()
            assertEquals(3, result)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertCountry_should_insert_single_entity() = runTest {
        dao.insertCountry(testCountry1)

        val result = dao.getCountryByCode("US")
        assertEquals("United States", result?.name)
    }

    @Test
    fun insertCountries_should_batch_insert_multiple_entities() = runTest {
        dao.insertCountries(listOf(testCountry1, testCountry2, testCountry3))

        val result1 = dao.getCountryByCode("US")
        val result2 = dao.getCountryByCode("FR")
        val result3 = dao.getCountryByCode("JP")

        assertEquals("United States", result1?.name)
        assertEquals("France", result2?.name)
        assertEquals("Japan", result3?.name)
    }

    @Test
    fun insertCountry_with_same_PK_should_replace_existing_entity() = runTest {
        dao.insertCountry(testCountry1)

        val updatedCountry = testCountry1.copy(
            name = "United States of America",
            visited = true,
            visitedDate = 1704067200000L,
            notes = "Updated",
            rating = 5,
        )

        dao.insertCountry(updatedCountry)

        val result = dao.getCountryByCode("US")
        assertEquals("United States of America", result?.name)
        assertTrue(result?.visited == true)
        assertEquals("Updated", result?.notes)
        assertEquals(5, result?.rating)
    }

    @Test
    fun updateCountry_should_update_entity() = runTest {
        dao.insertCountry(testCountry1)

        val updatedCountry = testCountry1.copy(
            notes = "Updated notes",
            rating = 4,
        )

        dao.updateCountry(updatedCountry)

        val result = dao.getCountryByCode("US")
        assertEquals("Updated notes", result?.notes)
        assertEquals(4, result?.rating)
    }

    @Test
    fun updateCountry_on_non_existent_entity_should_not_crash() = runTest {
        val nonExistentCountry = CountryEntity(
            code = "XX",
            name = "Non-existent",
            region = "Nowhere",
            visited = false,
            visitedDate = null,
            notes = "",
            rating = 0,
            flagEmoji = "",
        )

        dao.updateCountry(nonExistentCountry)

        val result = dao.getCountryByCode("XX")
        assertEquals(null, result)
    }

    @Test
    fun deleteCountry_should_delete_entity() = runTest {
        dao.insertCountry(testCountry1)

        dao.deleteCountry(testCountry1)

        val result = dao.getCountryByCode("US")
        assertEquals(null, result)
    }

    @Test
    fun deleteAllCountries_should_clear_table() = runTest {
        dao.insertCountries(listOf(testCountry1, testCountry2, testCountry3))

        dao.deleteAllCountries()

        dao.getTotalCount().test {
            val result = awaitItem()
            assertEquals(0, result)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllRegions_should_return_distinct_regions_ordered_ASC() = runTest {
        dao.insertCountries(listOf(testCountry1, testCountry2, testCountry3))

        dao.getAllRegions().test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertEquals("Asia", result[0])
            assertEquals("Europe", result[1])
            assertEquals("North America", result[2])
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun Flow_should_re_emit_when_new_entity_is_inserted() = runTest {
        dao.insertCountry(testCountry1)

        dao.getAllCountries().test {
            val firstResult = awaitItem()
            assertEquals(1, firstResult.size)

            dao.insertCountry(testCountry2)

            val secondResult = awaitItem()
            assertEquals(2, secondResult.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun Flow_should_re_emit_when_entity_is_updated() = runTest {
        dao.insertCountry(testCountry1)

        dao.getAllCountries().test {
            val firstResult = awaitItem()
            assertEquals("United States", firstResult[0].name)

            val updatedCountry = testCountry1.copy(name = "United States of America")
            dao.updateCountry(updatedCountry)

            val secondResult = awaitItem()
            assertEquals("United States of America", secondResult[0].name)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun Flow_should_re_emit_when_entity_is_deleted() = runTest {
        dao.insertCountries(listOf(testCountry1, testCountry2))

        dao.getAllCountries().test {
            val firstResult = awaitItem()
            assertEquals(2, firstResult.size)

            dao.deleteCountry(testCountry1)

            val secondResult = awaitItem()
            assertEquals(1, secondResult.size)
            assertEquals("France", secondResult[0].name)

            cancelAndIgnoreRemainingEvents()
        }
    }

    companion object {
        private val testCountry1 = CountryEntity(
            code = "US",
            name = "United States",
            region = "North America",
            visited = false,
            flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8",
        )
        private val testCountry2 = CountryEntity(
            code = "FR",
            name = "France",
            region = "Europe",
            visited = true,
            visitedDate = 1704067200000L,
            flagEmoji = "\uD83C\uDDEB\uD83C\uDDF7",
        )
        private val testCountry3 = CountryEntity(
            code = "JP",
            name = "Japan",
            region = "Asia",
            visited = true,
            visitedDate = 1703980800000L,
            flagEmoji = "\uD83C\uDDEF\uD83C\uDDF5",
        )
    }
}
