package com.tecruz.countrytracker.features.countrydetail.data.mapper

import com.tecruz.countrytracker.core.data.database.CountryEntity
import com.tecruz.countrytracker.features.countrydetail.domain.model.CountryDetail
import org.junit.Assert.assertEquals
import org.junit.Test

class CountryDetailMapperTest {

    @Test
    fun `toCountryDetail maps entity correctly`() {
        val entity = CountryEntity(
            code = "US",
            name = "United States",
            region = "North America",
            visited = true,
            visitedDate = System.currentTimeMillis(),
            flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8",
            rating = 5,
        )

        val result = entity.toCountryDetail()

        assertEquals("US", result.code)
        assertEquals("United States", result.name)
        assertEquals("North America", result.region)
        assertEquals(true, result.visited)
        assertEquals(5, result.rating)
        assertEquals("\uD83C\uDDFA\uD83C\uDDF8", result.flagEmoji)
    }

    @Test
    fun `toCountryDetail handles unvisited country`() {
        val entity = CountryEntity(
            code = "FR",
            name = "France",
            region = "Europe",
            visited = false,
            visitedDate = null,
            flagEmoji = "\uD83C\uDDEB\uD83C\uDDF7",
            rating = 0,
        )

        val result = entity.toCountryDetail()

        assertEquals("FR", result.code)
        assertEquals(false, result.visited)
        assertEquals(0, result.rating)
    }

    @Test
    fun `toEntity maps domain model correctly`() {
        val domain = CountryDetail(
            code = "JP",
            name = "Japan",
            region = "Asia",
            visited = true,
            visitedDate = 1705276800000L,
            notes = "Great food",
            rating = 4,
            flagEmoji = "\uD83C\uDDEF\uD83C\uDDF5",
        )

        val result = domain.toEntity()

        assertEquals("JP", result.code)
        assertEquals("Japan", result.name)
        assertEquals("Asia", result.region)
        assertEquals(true, result.visited)
        assertEquals(1705276800000L, result.visitedDate)
        assertEquals("Great food", result.notes)
        assertEquals(4, result.rating)
    }
}
