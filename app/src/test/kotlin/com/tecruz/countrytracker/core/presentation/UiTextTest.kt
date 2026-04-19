package com.tecruz.countrytracker.core.presentation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UiTextTest {

    @Test
    fun `DynamicString returns value in asString`() {
        val text = UiText.DynamicString("Hello World")
        assertEquals("Hello World", text.asString())
    }

    @Test
    fun `StringResource returns empty in asString`() {
        val text = UiText.StringResource(android.R.string.ok)
        assertEquals("", text.asString())
    }

    @Test
    fun `StringResource equals with same resourceId and args`() {
        val text1 = UiText.StringResource(android.R.string.ok, arrayOf("arg1"))
        val text2 = UiText.StringResource(android.R.string.ok, arrayOf("arg1"))
        assertEquals(text1, text2)
    }

    @Test
    fun `StringResource not equals with different args`() {
        val text1 = UiText.StringResource(android.R.string.ok, arrayOf("arg1"))
        val text2 = UiText.StringResource(android.R.string.ok, arrayOf("arg2"))
        assertTrue(text1 != text2)
    }

    @Test
    fun `StringResource hashCode consistent`() {
        val text = UiText.StringResource(android.R.string.ok, arrayOf("arg1"))
        assertEquals(text.hashCode(), text.hashCode())
    }

    @Test
    fun `DynamicString equals itself`() {
        val text1 = UiText.DynamicString("test")
        val text2 = UiText.DynamicString("test")
        assertEquals(text1, text2)
    }
}
