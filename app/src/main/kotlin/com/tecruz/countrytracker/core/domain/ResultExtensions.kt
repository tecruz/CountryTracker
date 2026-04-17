package com.tecruz.countrytracker.core.domain

inline fun <T, E : Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> = when (this) {
    is Result.Failure -> Result.Failure(error)
    is Result.Success -> Result.Success(map(this.data))
}

inline fun <T, E : Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> = when (this) {
    is Result.Failure -> this
    is Result.Success -> {
        action(this.data)
        this
    }
}

inline fun <T, E : Error> Result<T, E>.onFailure(action: (E) -> Unit): Result<T, E> = when (this) {
    is Result.Failure -> {
        action(error)
        this
    }
    is Result.Success -> this
}

fun <T, E : Error> Result<T, E>.asEmptyResult(): EmptyResult<E> = map { }
