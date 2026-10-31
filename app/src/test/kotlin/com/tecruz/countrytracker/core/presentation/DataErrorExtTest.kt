package com.tecruz.countrytracker.core.presentation

import com.tecruz.countrytracker.core.domain.DataError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the [DataError.toUiText] mapping.
 *
 * Every enum case is exercised so that the exhaustive `when` branches in
 * DataErrorExt are all evaluated.
 */
class DataErrorExtTest {

    @Test
    fun `every network error maps to a string resource`() {
        for (error in DataError.Network.entries) {
            val text = error.toUiText()

            assertTrue("Network.$error should map to a StringResource", text is UiText.StringResource)
            assertTrue(
                "Network.$error should map to a real resource id",
                (text as UiText.StringResource).resourceId != 0,
            )
        }
    }

    @Test
    fun `every local error maps to a string resource`() {
        for (error in DataError.Local.entries) {
            val text = error.toUiText()

            assertTrue("Local.$error should map to a StringResource", text is UiText.StringResource)
            assertTrue("Local.$error should map to a real resource id", (text as UiText.StringResource).resourceId != 0)
        }
    }

    @Test
    fun `network and local errors are mapped through separate branches`() {
        val network = (DataError.Network.NO_INTERNET as DataError).toUiText()
        val local = (DataError.Local.DISK_FULL as DataError).toUiText()

        assertTrue(network is UiText.StringResource)
        assertTrue(local is UiText.StringResource)
        assertTrue(
            "Network and local mappings should not resolve to the same resource",
            (network as UiText.StringResource).resourceId != (local as UiText.StringResource).resourceId,
        )
    }

    @Test
    fun `same underlying error maps consistently`() {
        assertEquals(
            (DataError.Network.NOT_FOUND as DataError).toUiText(),
            (DataError.Local.NOT_FOUND as DataError).toUiText(),
        )
    }
}
