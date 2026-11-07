package com.tecruz.countrytracker.features.countrylist.presentation

/**
 * Outcome of a read that the country list shows.
 *
 * [Ready] with an empty payload is legitimate missing data.
 * [Unavailable] means the read failed and must not be presented as an empty success.
 */
enum class DataReadStatus {
    Loading,
    Ready,
    Unavailable,
}
