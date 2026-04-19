package com.tecruz.countrytracker.core.domain

import org.junit.Assert.assertTrue
import org.junit.Test

class DataErrorTest {

    @Test
    fun `Network errors implement DataError`() {
        val errors = listOf(
            DataError.Network.BAD_REQUEST,
            DataError.Network.REQUEST_TIMEOUT,
            DataError.Network.UNAUTHORIZED,
            DataError.Network.FORBIDDEN,
            DataError.Network.NOT_FOUND,
            DataError.Network.CONFLICT,
            DataError.Network.TOO_MANY_REQUESTS,
            DataError.Network.NO_INTERNET,
            DataError.Network.PAYLOAD_TOO_LARGE,
            DataError.Network.SERVER_ERROR,
            DataError.Network.SERVICE_UNAVAILABLE,
            DataError.Network.SERIALIZATION,
            DataError.Network.UNKNOWN,
        )
        assertTrue(errors.all { it is DataError })
    }

    @Test
    fun `Local errors implement DataError`() {
        val errors = listOf(
            DataError.Local.DISK_FULL,
            DataError.Local.NOT_FOUND,
            DataError.Local.UNKNOWN,
        )
        assertTrue(errors.all { it is DataError })
    }

    @Test
    fun `each error enum has unique name`() {
        val networkNames = DataError.Network.entries.map { it.name }
        val localNames = DataError.Local.entries.map { it.name }
        assertTrue(networkNames.size == networkNames.distinct().size)
        assertTrue(localNames.size == localNames.distinct().size)
    }
}
