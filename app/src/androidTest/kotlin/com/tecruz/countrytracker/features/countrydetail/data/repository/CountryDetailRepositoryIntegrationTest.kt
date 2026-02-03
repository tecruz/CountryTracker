package com.tecruz.countrytracker.features.countrydetail.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tecruz.countrytracker.core.data.database.CountryDatabase
import com.tecruz.countrytracker.core.data.database.CountryEntity
import com.tecruz.countrytracker.features.countrydetail.data.mapper.toEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CountryDetailRepositoryIntegrationTest {

    private lateinit var database: CountryDatabase
    private lateinit var dao: com.tecruz.countrytracker.core.data.database.CountryDao
    private lateinit var repository: CountryDetailRepositoryImpl

    private val testEntity = CountryEntity(
        code = "US",
        name = "United States",
        region = "North America",
        visited = false,
        visitedDate = null,
        notes = "",
        rating = 0,
        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8",
    )

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CountryDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = database.countryDao()
        repository = CountryDetailRepositoryImpl(dao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getCountryByCode_should_return_CountryDetail_with_all_8_fields_mapped_correctly() = runTest {
        dao.insertCountry(testEntity)

        val result = repository.getCountryByCode("US")

        assertEquals("US", result?.code)
        assertEquals("United States", result?.name)
        assertEquals("North America", result?.region)
        assertFalse(result?.visited ?: true)
        assertNull(result?.visitedDate)
        assertEquals("", result?.notes)
        assertEquals(0, result?.rating)
        assertEquals("\uD83C\uDDFA\uD83C\uDDF8", result?.flagEmoji)
    }

    @Test
    fun getCountryByCode_should_return_null_for_non_existent_code() = runTest {
        dao.insertCountry(testEntity)

        val result = repository.getCountryByCode("XX")

        assertEquals(null, result)
    }

    @Test
    fun updateCountry_should_persist_single_field_change() = runTest {
        dao.insertCountry(testEntity)
        val country = repository.getCountryByCode("US")!!

        val updatedCountry = country.copy(notes = "New notes")
        repository.updateCountry(updatedCountry)

        val result = repository.getCountryByCode("US")
        assertEquals("New notes", result?.notes)
        assertEquals("United States", result?.name)
        assertEquals(0, result?.rating)
    }

    @Test
    fun updateCountry_should_persist_multiple_field_changes() = runTest {
        dao.insertCountry(testEntity)
        val country = repository.getCountryByCode("US")!!

        val updatedCountry = country.copy(
            notes = "Updated notes",
            rating = 5,
            visited = true,
        )
        repository.updateCountry(updatedCountry)

        val result = repository.getCountryByCode("US")
        assertEquals("Updated notes", result?.notes)
        assertEquals(5, result?.rating)
        assertTrue(result?.visited ?: false)
        assertEquals("United States", result?.name)
    }

    @Test
    fun markAsVisited_with_explicit_params_should_set_all_fields_correctly() = runTest {
        dao.insertCountry(testEntity)
        val country = repository.getCountryByCode("US")!!
        val date = 1704067200000L
        val notes = "Great trip!"
        val rating = 5

        repository.markAsVisited(country, date, notes, rating)

        val result = repository.getCountryByCode("US")
        assertTrue(result?.visited ?: false)
        assertEquals(date, result?.visitedDate)
        assertEquals(notes, result?.notes)
        assertEquals(rating, result?.rating)
    }

    @Test
    fun markAsVisited_with_default_params_should_apply_defaults() = runTest {
        dao.insertCountry(testEntity)
        val country = repository.getCountryByCode("US")!!
        val date = 1704067200000L

        repository.markAsVisited(country, date)

        val result = repository.getCountryByCode("US")
        assertTrue(result?.visited ?: false)
        assertEquals(date, result?.visitedDate)
        assertEquals("", result?.notes)
        assertEquals(0, result?.rating)
    }

    @Test
    fun markAsUnvisited_should_reset_all_visit_related_fields() = runTest {
        val visitedEntity = testEntity.copy(
            visited = true,
            visitedDate = 1704067200000L,
            notes = "Had a great time",
            rating = 5,
        )
        dao.insertCountry(visitedEntity)
        val country = repository.getCountryByCode("US")!!

        repository.markAsUnvisited(country)

        val result = repository.getCountryByCode("US")
        assertFalse(result?.visited ?: true)
        assertNull(result?.visitedDate)
        assertEquals("", result?.notes)
        assertEquals(0, result?.rating)
    }

    @Test
    fun bidirectional_mapping_roundtrip_should_preserve_all_fields() = runTest {
        val originalEntity = CountryEntity(
            code = "FR",
            name = "France",
            region = "Europe",
            visited = true,
            visitedDate = 1704067200000L,
            notes = "Wonderful trip",
            rating = 5,
            flagEmoji = "\uD83C\uDDEB\uD83C\uDDF7",
        )
        dao.insertCountry(originalEntity)

        val countryDetail = repository.getCountryByCode("FR")!!
        val roundtripEntity = countryDetail.toEntity()

        assertEquals(originalEntity.code, roundtripEntity.code)
        assertEquals(originalEntity.name, roundtripEntity.name)
        assertEquals(originalEntity.region, roundtripEntity.region)
        assertEquals(originalEntity.visited, roundtripEntity.visited)
        assertEquals(originalEntity.visitedDate, roundtripEntity.visitedDate)
        assertEquals(originalEntity.notes, roundtripEntity.notes)
        assertEquals(originalEntity.rating, roundtripEntity.rating)
        assertEquals(originalEntity.flagEmoji, roundtripEntity.flagEmoji)
    }

    @Test
    fun updateCountry_should_replace_existing_entity_via_OnConflictStrategy() = runTest {
        dao.insertCountry(testEntity)
        val originalCountry = repository.getCountryByCode("US")!!

        val updatedCountry = originalCountry.copy(
            name = "United States of America",
            visited = true,
            visitedDate = 1704067200000L,
            notes = "Updated via upsert",
            rating = 5,
        )
        repository.updateCountry(updatedCountry)

        val result = repository.getCountryByCode("US")
        assertEquals("United States of America", result?.name)
        assertTrue(result?.visited ?: false)
        assertEquals(1704067200000L, result?.visitedDate)
        assertEquals("Updated via upsert", result?.notes)
        assertEquals(5, result?.rating)
    }
}
