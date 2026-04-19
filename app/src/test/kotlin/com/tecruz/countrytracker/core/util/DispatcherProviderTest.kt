package com.tecruz.countrytracker.core.util

import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertSame
import org.junit.Test

class DispatcherProviderTest {

    @Test
    fun `io dispatcher is IO`() {
        val provider = DispatcherProvider()
        assertSame(Dispatchers.IO, provider.io)
    }

    @Test
    fun `default dispatcher is Default`() {
        val provider = DispatcherProvider()
        assertSame(Dispatchers.Default, provider.default)
    }

    @Test
    fun `main dispatcher is Main`() {
        val provider = DispatcherProvider()
        assertSame(Dispatchers.Main, provider.main)
    }

    @Test
    fun `unconfined dispatcher is Unconfined`() {
        val provider = DispatcherProvider()
        assertSame(Dispatchers.Unconfined, provider.unconfined)
    }

    @Test
    fun `different instances have same dispatchers`() {
        val provider1 = DispatcherProvider()
        val provider2 = DispatcherProvider()
        assertSame(provider1.io, provider2.io)
        assertSame(provider1.main, provider2.main)
    }
}
