package com.tecruz.countrytracker.core.presentation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.stringResource

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

/**
 * Resolves this text with Android resources. Use from a [Composable] via [resolve].
 */
fun UiText.resolve(context: Context): String = when (this) {
    is UiText.DynamicString -> value
    is UiText.StringResource -> if (args.isEmpty()) {
        context.getString(resourceId)
    } else {
        context.getString(resourceId, *args)
    }
}

@Composable
fun UiText.resolve(): String = when (this) {
    is UiText.DynamicString -> value
    is UiText.StringResource -> if (args.isEmpty()) {
        stringResource(resourceId)
    } else {
        stringResource(resourceId, *args)
    }
}
