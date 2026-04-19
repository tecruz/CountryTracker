package com.tecruz.countrytracker.features.countrylist.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CountryListItemTest {

    @Test
    fun `creates with all fields`() {
        val item = CountryListItem(
            code = "US",
            name = "United States",
            region = "North America",
            visited = true,
            flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8",
        )

        assertEquals("US", item.code)
        assertEquals("United States", item.name)
        assertEquals("North America", item.region)
        assertTrue(item.visited)
    }

    @Test
    fun `unvisited defaults to false`() {
        val item = CountryListItem(
            code = "FR",
            name = "France",
            region = "Europe",
            visited = false,
            flagEmoji = "\uD83C\uDDEB\uD83C\uDDF7",
        )

        assertFalse(item.visited)
    }

    @Test
    fun `equals compares all fields`() {
        val item1 = CountryListItem("US", "US", "NA", true, "flag")
        val item2 = CountryListItem("US", "US", "NA", true, "flag")
        assertEquals(item1, item2)
    }

    @Test
    fun `copy modifies visited`() {
        val original = CountryListItem("US", "US", "NA", false, "flag")
        val modified = original.copy(visited = true)

        assertEquals(original.code, modified.code)
        assertTrue(modified.visited)
    }
}
