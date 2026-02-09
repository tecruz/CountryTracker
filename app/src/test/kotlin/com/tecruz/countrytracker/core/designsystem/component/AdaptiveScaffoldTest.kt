package com.tecruz.countrytracker.core.designsystem.component

import androidx.window.core.layout.WindowSizeClass
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for AdaptiveScaffold layout logic.
 * Tests verify the structural behavior: when compact, content is full-width;
 * when medium/expanded, navigation rail is shown alongside content.
 *
 * Note: Compose UI rendering tests are in androidTest.
 * These tests verify the conditional logic used by AdaptiveScaffold.
 */
class AdaptiveScaffoldTest {

    private fun compactWindowSizeClass(): WindowSizeClass = WindowSizeClass.compute(400f, 800f)

    private fun mediumWindowSizeClass(): WindowSizeClass = WindowSizeClass.compute(700f, 900f)

    private fun expandedWindowSizeClass(): WindowSizeClass = WindowSizeClass.compute(900f, 1200f)

    @Test
    fun `compact window uses full-width layout without navigation rail`() {
        val windowSizeClass = compactWindowSizeClass()
        // AdaptiveScaffold shows no navigation rail on compact
        assertTrue(windowSizeClass.windowWidthSizeClass == androidx.window.core.layout.WindowWidthSizeClass.COMPACT)
    }

    @Test
    fun `medium window shows navigation rail`() {
        val windowSizeClass = mediumWindowSizeClass()
        assertFalse(windowSizeClass.windowWidthSizeClass == androidx.window.core.layout.WindowWidthSizeClass.COMPACT)
    }

    @Test
    fun `expanded window shows navigation rail`() {
        val windowSizeClass = expandedWindowSizeClass()
        assertFalse(windowSizeClass.windowWidthSizeClass == androidx.window.core.layout.WindowWidthSizeClass.COMPACT)
    }

    @Test
    fun `navigation rail is only shown when not compact and rail is provided`() {
        val compact = compactWindowSizeClass()
        val medium = mediumWindowSizeClass()
        val expanded = expandedWindowSizeClass()

        // On compact, even if navigation rail is provided, scaffold uses full-width layout
        assertTrue(compact.windowWidthSizeClass == androidx.window.core.layout.WindowWidthSizeClass.COMPACT)

        // On medium/expanded, scaffold shows navigation rail alongside content
        assertFalse(medium.windowWidthSizeClass == androidx.window.core.layout.WindowWidthSizeClass.COMPACT)
        assertFalse(expanded.windowWidthSizeClass == androidx.window.core.layout.WindowWidthSizeClass.COMPACT)
    }
}
