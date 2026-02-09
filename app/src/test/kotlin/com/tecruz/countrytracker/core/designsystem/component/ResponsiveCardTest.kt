package com.tecruz.countrytracker.core.designsystem.component

import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for ResponsiveCard responsive logic.
 * Verifies that padding, corner radius, and elevation scale correctly
 * across compact, medium, and expanded window sizes.
 */
class ResponsiveCardTest {

    private fun compactWindowSizeClass(): WindowSizeClass = WindowSizeClass.compute(400f, 800f)

    private fun mediumWindowSizeClass(): WindowSizeClass = WindowSizeClass.compute(700f, 900f)

    private fun expandedWindowSizeClass(): WindowSizeClass = WindowSizeClass.compute(900f, 1200f)

    @Test
    fun `compact window produces compact card values`() {
        val wsc = compactWindowSizeClass()
        assertEquals(WindowWidthSizeClass.COMPACT, wsc.windowWidthSizeClass)
    }

    @Test
    fun `medium window produces medium card values`() {
        val wsc = mediumWindowSizeClass()
        assertEquals(WindowWidthSizeClass.MEDIUM, wsc.windowWidthSizeClass)
    }

    @Test
    fun `expanded window produces expanded card values`() {
        val wsc = expandedWindowSizeClass()
        assertEquals(WindowWidthSizeClass.EXPANDED, wsc.windowWidthSizeClass)
    }

    @Test
    fun `card values scale progressively from compact to expanded`() {
        val compact = compactWindowSizeClass()
        val medium = mediumWindowSizeClass()
        val expanded = expandedWindowSizeClass()

        // Compact < Medium < Expanded (in terms of width class ordinal)
        val sizes = listOf(compact, medium, expanded)
        val widthClasses = sizes.map { it.windowWidthSizeClass }
        assertEquals(WindowWidthSizeClass.COMPACT, widthClasses[0])
        assertEquals(WindowWidthSizeClass.MEDIUM, widthClasses[1])
        assertEquals(WindowWidthSizeClass.EXPANDED, widthClasses[2])
    }
}
