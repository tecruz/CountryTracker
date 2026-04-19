package com.tecruz.countrytracker.core.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultExtensionsTest {

    @Test
    fun `map transforms success data`() {
        val result: Result<Int, DataError.Local> = Result.Success(5)
        val mapped = result.map { it * 2 }

        mapped.onSuccess { assertEquals(10, it) }
    }

    @Test
    fun `map preserves failure`() {
        val result: Result<Int, DataError.Local> = Result.Failure(DataError.Local.NOT_FOUND)
        val mapped = result.map { it * 2 }

        mapped.onFailure { assertTrue(it is DataError.Local) }
    }

    @Test
    fun `onSuccess executes action on success`() {
        var actionExecuted = false
        val result: Result<Int, DataError.Local> = Result.Success(5)

        result.onSuccess {
            actionExecuted = true
            assertEquals(5, it)
        }

        assertTrue(actionExecuted)
    }

    @Test
    fun `onSuccess does nothing on failure`() {
        var actionExecuted = false
        val result: Result<Int, DataError.Local> = Result.Failure(DataError.Local.NOT_FOUND)

        result.onSuccess {
            actionExecuted = true
        }

        assertTrue(!actionExecuted)
    }

    @Test
    fun `onFailure executes action on failure`() {
        var actionExecuted = false
        val result: Result<Int, DataError.Local> = Result.Failure(DataError.Local.NOT_FOUND)

        result.onFailure {
            actionExecuted = true
            assertTrue(it is DataError.Local)
        }

        assertTrue(actionExecuted)
    }

    @Test
    fun `onFailure does nothing on success`() {
        var actionExecuted = false
        val result: Result<Int, DataError.Local> = Result.Success(5)

        result.onFailure {
            actionExecuted = true
        }

        assertTrue(!actionExecuted)
    }

    @Test
    fun `asEmptyResult converts success to EmptyResult`() {
        val result: Result<Int, DataError.Local> = Result.Success(5)
        val empty = result.asEmptyResult()

        empty.onSuccess { assertEquals(Unit, it) }
    }

    @Test
    fun `asEmptyResult preserves failure`() {
        val result: Result<Int, DataError.Local> = Result.Failure(DataError.Local.NOT_FOUND)
        val empty = result.asEmptyResult()

        empty.onFailure { assertTrue(it is DataError.Local) }
    }
}
