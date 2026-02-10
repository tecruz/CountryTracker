package com.tecruz.countrytracker.core.util

import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WindowSizeClassExtTest {

    private fun compactWindowSizeClass(): WindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 400f, heightDp = 800f) // Compact: < 600dp width

    private fun mediumWindowSizeClass(): WindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 700f, heightDp = 900f) // Medium: 600-839dp width

    private fun expandedWindowSizeClass(): WindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 900f, heightDp = 1200f) // Expanded: >= 840dp width

    @Test
    fun `isCompact returns true for compact window`() {
        assertTrue(compactWindowSizeClass().isCompact())
    }

    @Test
    fun `isCompact returns false for medium window`() {
        assertFalse(mediumWindowSizeClass().isCompact())
    }

    @Test
    fun `isCompact returns false for expanded window`() {
        assertFalse(expandedWindowSizeClass().isCompact())
    }

    @Test
    fun `isMedium returns true for medium window`() {
        assertTrue(mediumWindowSizeClass().isMedium())
    }

    @Test
    fun `isMedium returns false for compact window`() {
        assertFalse(compactWindowSizeClass().isMedium())
    }

    @Test
    fun `isExpanded returns true for expanded window`() {
        assertTrue(expandedWindowSizeClass().isExpanded())
    }

    @Test
    fun `isExpanded returns false for compact window`() {
        assertFalse(compactWindowSizeClass().isExpanded())
    }

    @Test
    fun `isExpanded returns false for medium window`() {
        assertFalse(mediumWindowSizeClass().isExpanded())
    }

    @Test
    fun `isMedium returns false for expanded window`() {
        assertFalse(expandedWindowSizeClass().isMedium())
    }

    @Test
    fun `gridColumns returns 1 for compact`() {
        assertEquals(1, compactWindowSizeClass().gridColumns())
    }

    @Test
    fun `gridColumns returns 2 for medium`() {
        assertEquals(2, mediumWindowSizeClass().gridColumns())
    }

    @Test
    fun `gridColumns returns 3 for expanded`() {
        assertEquals(3, expandedWindowSizeClass().gridColumns())
    }

    @Test
    fun `horizontalPadding returns 16dp for compact`() {
        assertEquals(16.dp, compactWindowSizeClass().horizontalPadding())
    }

    @Test
    fun `horizontalPadding returns 24dp for medium`() {
        assertEquals(24.dp, mediumWindowSizeClass().horizontalPadding())
    }

    @Test
    fun `horizontalPadding returns 32dp for expanded`() {
        assertEquals(32.dp, expandedWindowSizeClass().horizontalPadding())
    }

    @Test
    fun `contentPadding returns 20dp for compact`() {
        assertEquals(20.dp, compactWindowSizeClass().contentPadding())
    }

    @Test
    fun `contentPadding returns 24dp for medium`() {
        assertEquals(24.dp, mediumWindowSizeClass().contentPadding())
    }

    @Test
    fun `contentPadding returns 32dp for expanded`() {
        assertEquals(32.dp, expandedWindowSizeClass().contentPadding())
    }

    @Test
    fun `itemSpacing returns 12dp for compact`() {
        assertEquals(12.dp, compactWindowSizeClass().itemSpacing())
    }

    @Test
    fun `itemSpacing returns 14dp for medium`() {
        assertEquals(14.dp, mediumWindowSizeClass().itemSpacing())
    }

    @Test
    fun `itemSpacing returns 16dp for expanded`() {
        assertEquals(16.dp, expandedWindowSizeClass().itemSpacing())
    }

    @Test
    fun `minTouchTarget returns 48dp for compact`() {
        assertEquals(48.dp, compactWindowSizeClass().minTouchTarget())
    }

    @Test
    fun `minTouchTarget returns 52dp for medium`() {
        assertEquals(52.dp, mediumWindowSizeClass().minTouchTarget())
    }

    @Test
    fun `minTouchTarget returns 56dp for expanded`() {
        assertEquals(56.dp, expandedWindowSizeClass().minTouchTarget())
    }
}
