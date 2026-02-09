package com.tecruz.countrytracker.features.countrylist.presentation

import androidx.window.core.layout.WindowSizeClass
import com.tecruz.countrytracker.core.util.itemSpacing
import com.tecruz.countrytracker.core.util.minTouchTarget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * T015: Unit test for CountryListRow responsive behavior.
 * Tests that the responsive values for CountryListRow (touch targets, spacing)
 * are correct for compact screen sizes.
 */
class CountryListRowResponsiveTest {

    private fun compactWindowSizeClass(): WindowSizeClass = WindowSizeClass.compute(375f, 667f)

    private fun mediumWindowSizeClass(): WindowSizeClass = WindowSizeClass.compute(700f, 900f)

    private fun expandedWindowSizeClass(): WindowSizeClass = WindowSizeClass.compute(900f, 1200f)

    @Test
    fun `compact screen has minimum 48dp touch target`() {
        val minTarget = compactWindowSizeClass().minTouchTarget()
        assertTrue("Touch target should be at least 48dp", minTarget.value >= 48f)
    }

    @Test
    fun `medium screen has larger touch target than compact`() {
        val compactTarget = compactWindowSizeClass().minTouchTarget()
        val mediumTarget = mediumWindowSizeClass().minTouchTarget()
        assertTrue(
            "Medium touch target (${mediumTarget.value}) should be >= compact (${compactTarget.value})",
            mediumTarget >= compactTarget,
        )
    }

    @Test
    fun `expanded screen has largest touch target`() {
        val mediumTarget = mediumWindowSizeClass().minTouchTarget()
        val expandedTarget = expandedWindowSizeClass().minTouchTarget()
        assertTrue(
            "Expanded touch target (${expandedTarget.value}) should be >= medium (${mediumTarget.value})",
            expandedTarget >= mediumTarget,
        )
    }

    @Test
    fun `compact screen item spacing is 12dp`() {
        val spacing = compactWindowSizeClass().itemSpacing()
        assertEquals(12f, spacing.value, 0.01f)
    }

    @Test
    fun `item spacing scales with screen size`() {
        val compactSpacing = compactWindowSizeClass().itemSpacing()
        val mediumSpacing = mediumWindowSizeClass().itemSpacing()
        val expandedSpacing = expandedWindowSizeClass().itemSpacing()

        assertTrue("Medium spacing should be > compact", mediumSpacing > compactSpacing)
        assertTrue("Expanded spacing should be > medium", expandedSpacing > mediumSpacing)
    }
}
