package com.tecruz.countrytracker.features.countrylist.data.mapper

import com.tecruz.countrytracker.core.data.database.CountryEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class CountryListMapperTest {

    @Test
    fun `toCountryListItem maps entity correctly`() {
        val entity = CountryEntity(
            code = "US",
            name = "United States",
            region = "North America",
            visited = true,
            visitedDate = System.currentTimeMillis(),
            flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8",
            rating = 5,
        )

        val result = entity.toCountryListItem()

        assertEquals("US", result.code)
        assertEquals("United States", result.name)
        assertEquals("North America", result.region)
        assertEquals(true, result.visited)
        assertEquals("\uD83C\uDDFA\uD83C\uDDF8", result.flagEmoji)
    }

    @Test
    fun `toCountryListItem handles unvisited country`() {
        val entity = CountryEntity(
            code = "FR",
            name = "France",
            region = "Europe",
            visited = false,
            visitedDate = null,
            flagEmoji = "\uD83C\uDDEB\uD83C\uDDF7",
            rating = 0,
        )

        val result = entity.toCountryListItem()

        assertEquals("FR", result.code)
        assertEquals("France", result.name)
        assertEquals("Europe", result.region)
        assertEquals(false, result.visited)
    }
}
