package com.tecruz.countrytracker.features.countrylist.presentation

import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [CountryListUiState].
 *
 * The screen state is a plain data class, so these tests exercise the
 * generated constructor, component accessors, copy, equality and toString.
 */
class CountryListUiStateTest {

    private val brazil = CountryListItem(
        code = "BR",
        name = "Brazil",
        region = "South America",
        visited = true,
        flagEmoji = "\uD83C\uDDE7\uD83C\uDDF7",
    )

    @Test
    fun `defaults describe an empty loading screen`() {
        val state = CountryListUiState()

        assertTrue(state.countries.isEmpty())
        assertTrue(state.visitedCountryCodes.isEmpty())
        assertEquals(0, state.visitedCount)
        assertEquals(0, state.totalCount)
        assertEquals(0, state.percentage)
        assertEquals("All", state.selectedRegion)
        assertEquals("", state.searchQuery)
        assertFalse(state.showOnlyVisited)
        assertTrue(state.isLoading)
        assertTrue(state.allRegions.isEmpty())
        assertEquals(null, state.error)
    }

    @Test
    fun `holds every provided value`() {
        val state = CountryListUiState(
            countries = listOf(brazil),
            visitedCountryCodes = setOf("BR"),
            visitedCount = 1,
            totalCount = 195,
            percentage = 1,
            selectedRegion = "South America",
            searchQuery = "bra",
            showOnlyVisited = true,
            isLoading = false,
            allRegions = listOf("South America"),
            error = "boom",
        )

        assertEquals(listOf(brazil), state.countries)
        assertEquals(setOf("BR"), state.visitedCountryCodes)
        assertEquals(1, state.visitedCount)
        assertEquals(195, state.totalCount)
        assertEquals(1, state.percentage)
        assertEquals("South America", state.selectedRegion)
        assertEquals("bra", state.searchQuery)
        assertTrue(state.showOnlyVisited)
        assertFalse(state.isLoading)
        assertEquals(listOf("South America"), state.allRegions)
        assertEquals("boom", state.error)
    }

    @Test
    fun `component accessors expose each field`() {
        val state = CountryListUiState(
            countries = listOf(brazil),
            visitedCountryCodes = setOf("BR"),
            visitedCount = 1,
            totalCount = 195,
            percentage = 1,
            selectedRegion = "South America",
            searchQuery = "bra",
            showOnlyVisited = true,
            isLoading = false,
            allRegions = listOf("South America"),
            error = "boom",
        )

        val (countries, visitedCountryCodes, visitedCount, totalCount, percentage) = state

        assertEquals(listOf(brazil), countries)
        assertEquals(setOf("BR"), visitedCountryCodes)
        assertEquals(1, visitedCount)
        assertEquals(195, totalCount)
        assertEquals(1, percentage)
        assertEquals("South America", state.selectedRegion)
        assertEquals("bra", state.searchQuery)
        assertTrue(state.showOnlyVisited)
        assertFalse(state.isLoading)
        assertEquals(listOf("South America"), state.allRegions)
        assertEquals("boom", state.error)
    }

    @Test
    fun `copy replaces only the requested fields`() {
        val original = CountryListUiState(countries = listOf(brazil), isLoading = false)
        val updated = original.copy(searchQuery = "b", error = "oops")

        assertEquals(listOf(brazil), updated.countries)
        assertFalse(updated.isLoading)
        assertEquals("b", updated.searchQuery)
        assertEquals("oops", updated.error)
        assertNotEquals(original, updated)
    }

    @Test
    fun `equality and hashCode are value based`() {
        val a = CountryListUiState(countries = listOf(brazil), visitedCount = 3, isLoading = false)
        val b = CountryListUiState(countries = listOf(brazil), visitedCount = 3, isLoading = false)

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertNotEquals(a, a.copy(visitedCount = 4))
    }

    @Test
    fun `toString includes the state values`() {
        val text = CountryListUiState(countries = listOf(brazil), isLoading = false).toString()

        assertTrue(text.contains("CountryListUiState"))
        assertTrue(text.contains("BR"))
        assertTrue(text.contains("isLoading=false"))
    }
}
