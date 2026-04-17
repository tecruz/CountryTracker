package com.tecruz.countrytracker.core.presentation

import androidx.compose.runtime.Immutable

sealed interface UiText {
    @Immutable
    data class DynamicString(val value: String) : UiText

    @Immutable
    class StringResource(val resourceId: Int, val args: Array<Any> = emptyArray()) : UiText {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as StringResource
            return resourceId == other.resourceId && args.contentEquals(other.args)
        }

        override fun hashCode(): Int {
            var result = resourceId
            result = 31 * result + args.contentHashCode()
            return result
        }
    }
}

fun UiText.asString(): String = when (this) {
    is UiText.DynamicString -> value
    is UiText.StringResource -> "" // Will be resolved in composable
}
