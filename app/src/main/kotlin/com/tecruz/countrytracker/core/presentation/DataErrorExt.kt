package com.tecruz.countrytracker.core.presentation

import com.tecruz.countrytracker.R
import com.tecruz.countrytracker.core.domain.DataError

fun DataError.toUiText(): UiText = when (this) {
    is DataError.Network -> networkToUiText(this)
    is DataError.Local -> localToUiText(this)
}

private fun networkToUiText(error: DataError.Network): UiText = when (error) {
    DataError.Network.BAD_REQUEST -> UiText.StringResource(R.string.error_bad_request)
    DataError.Network.REQUEST_TIMEOUT -> UiText.StringResource(R.string.error_request_timeout)
    DataError.Network.UNAUTHORIZED -> UiText.StringResource(R.string.error_unauthorized)
    DataError.Network.FORBIDDEN -> UiText.StringResource(R.string.error_forbidden)
    DataError.Network.NOT_FOUND -> UiText.StringResource(R.string.error_not_found)
    DataError.Network.CONFLICT -> UiText.StringResource(R.string.error_conflict)
    DataError.Network.TOO_MANY_REQUESTS -> UiText.StringResource(R.string.error_too_many_requests)
    DataError.Network.NO_INTERNET -> UiText.StringResource(R.string.error_no_internet)
    DataError.Network.PAYLOAD_TOO_LARGE -> UiText.StringResource(R.string.error_payload_too_large)
    DataError.Network.SERVER_ERROR -> UiText.StringResource(R.string.error_server)
    DataError.Network.SERVICE_UNAVAILABLE -> UiText.StringResource(R.string.error_service_unavailable)
    DataError.Network.SERIALIZATION -> UiText.StringResource(R.string.error_serialization)
    DataError.Network.UNKNOWN -> UiText.StringResource(R.string.error_unknown)
}

private fun localToUiText(error: DataError.Local): UiText = when (error) {
    DataError.Local.DISK_FULL -> UiText.StringResource(R.string.error_disk_full)
    DataError.Local.NOT_FOUND -> UiText.StringResource(R.string.error_not_found)
    DataError.Local.UNKNOWN -> UiText.StringResource(R.string.error_unknown)
}
