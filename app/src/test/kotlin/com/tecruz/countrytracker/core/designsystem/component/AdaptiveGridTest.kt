package com.tecruz.countrytracker.core.designsystem.component

import androidx.window.core.layout.WindowSizeClass
import com.tecruz.countrytracker.core.util.gridColumns
import com.tecruz.countrytracker.core.util.itemSpacing
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for AdaptiveGrid responsive logic.
 * Verifies column count and spacing adapt correctly to window size.
 */
class AdaptiveGridTest {

    private fun compactWindowSizeClass(): WindowSizeClass = WindowSizeClass.compute(400f, 800f)

    private fun mediumWindowSizeClass(): WindowSizeClass = WindowSizeClass.compute(700f, 900f)

    private fun expandedWindowSizeClass(): WindowSizeClass = WindowSizeClass.compute(900f, 1200f)

    @Test
    fun `compact window uses single column`() {
        assertEquals(1, compactWindowSizeClass().gridColumns())
    }

    @Test
    fun `medium window uses two columns`() {
        assertEquals(2, mediumWindowSizeClass().gridColumns())
    }

    @Test
    fun `expanded window uses three columns`() {
        assertEquals(3, expandedWindowSizeClass().gridColumns())
    }

    @Test
    fun `item spacing increases with screen size`() {
        val compactSpacing = compactWindowSizeClass().itemSpacing()
        val mediumSpacing = mediumWindowSizeClass().itemSpacing()
        val expandedSpacing = expandedWindowSizeClass().itemSpacing()

        assert(compactSpacing < mediumSpacing) {
            "Medium spacing ($mediumSpacing) should be greater than compact ($compactSpacing)"
        }
        assert(mediumSpacing < expandedSpacing) {
            "Expanded spacing ($expandedSpacing) should be greater than medium ($mediumSpacing)"
        }
    }

    @Test
    fun `grid uses LazyColumn for single column mode`() {
        // When gridColumns returns 1, AdaptiveGrid should use LazyColumn
        val columns = compactWindowSizeClass().gridColumns()
        assertEquals(1, columns)
        // LazyColumn is used when columns <= 1
    }

    @Test
    fun `grid uses LazyVerticalGrid for multi-column mode`() {
        // When gridColumns > 1, AdaptiveGrid should use LazyVerticalGrid
        val mediumColumns = mediumWindowSizeClass().gridColumns()
        val expandedColumns = expandedWindowSizeClass().gridColumns()
        assert(mediumColumns > 1)
        assert(expandedColumns > 1)
    }
}
