package com.tecruz.countrytracker.features.countrydetail.presentation

import com.tecruz.countrytracker.features.countrydetail.presentation.model.CountryDetailUi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [CountryDetailUiState].
 *
 * Exercises the generated constructor, component accessors, copy, equality
 * and toString of the detail screen state.
 */
class CountryDetailUiStateTest {

    private val country = CountryDetailUi(
        code = "BR",
        name = "Brazil",
        region = "South America",
        visited = true,
        visitedDate = 1704067200000L,
        visitedDateFormatted = "January 01, 2024",
        notes = "Trip",
        rating = 5,
        flagEmoji = "\uD83C\uDDE7\uD83C\uDDF7",
    )

    @Test
    fun `defaults describe an empty loading screen`() {
        val state = CountryDetailUiState()

        assertNull(state.country)
        assertTrue(state.isLoading)
        assertNull(state.error)
        assertFalse(state.isSaving)
    }

    @Test
    fun `holds every provided value`() {
        val state = CountryDetailUiState(
            country = country,
            isLoading = false,
            error = "failed",
            isSaving = true,
        )

        assertEquals(country, state.country)
        assertFalse(state.isLoading)
        assertEquals("failed", state.error)
        assertTrue(state.isSaving)
    }

    @Test
    fun `component accessors expose each field`() {
        val state = CountryDetailUiState(country = country, isLoading = false, isSaving = true)
        val (stateCountry, stateLoading, stateError, stateSaving) = state

        assertEquals(country, stateCountry)
        assertFalse(stateLoading)
        assertNull(stateError)
        assertTrue(stateSaving)
    }

    @Test
    fun `copy replaces only the requested fields`() {
        val original = CountryDetailUiState(country = country, isLoading = false)
        val updated = original.copy(isSaving = true, error = "network")

        assertEquals(country, updated.country)
        assertFalse(updated.isLoading)
        assertTrue(updated.isSaving)
        assertEquals("network", updated.error)
        assertNotEquals(original, updated)
    }

    @Test
    fun `equality and hashCode are value based`() {
        val a = CountryDetailUiState(country = country, isLoading = false, isSaving = true)
        val b = CountryDetailUiState(country = country, isLoading = false, isSaving = true)

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertNotEquals(a, a.copy(isSaving = false))
    }

    @Test
    fun `toString includes the state values`() {
        val text = CountryDetailUiState(country = country, isLoading = false).toString()

        assertTrue(text.contains("CountryDetailUiState"))
        assertTrue(text.contains("BR"))
        assertTrue(text.contains("isLoading=false"))
    }
}
