package com.tecruz.countrytracker.core.designsystem.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.tecruz.countrytracker.features.countrydetail.presentation.model.CountryDetailUi
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem

/**
 * Sample [CountryListItem] instances for previews.
 */
object PreviewCountryListItems {
    val visited = CountryListItem(
        code = "JP",
        name = "Japan",
        region = "Asia",
        visited = true,
        flagEmoji = "\uD83C\uDDEF\uD83C\uDDF5",
    )
    val unvisited = CountryListItem(
        code = "BR",
        name = "Brazil",
        region = "Americas",
        visited = false,
        flagEmoji = "\uD83C\uDDE7\uD83C\uDDF7",
    )
    val longName = CountryListItem(
        code = "GB",
        name = "United Kingdom of Great Britain and Northern Ireland",
        region = "Europe",
        visited = true,
        flagEmoji = "\uD83C\uDDEC\uD83C\uDDE7",
    )
    val all = listOf(visited, unvisited, longName)
}

/**
 * [PreviewParameterProvider] for [CountryListItem] — visited and unvisited states.
 */
class CountryListItemPreviewProvider : PreviewParameterProvider<CountryListItem> {
    override val values = PreviewCountryListItems.all.asSequence()
}

/**
 * Sample [CountryDetailUi] instances for previews.
 */
object PreviewCountryDetails {
    val visitedWithNotes = CountryDetailUi(
        code = "JP",
        name = "Japan",
        region = "Asia",
        visited = true,
        visitedDate = 1_700_000_000_000L,
        visitedDateFormatted = "November 14, 2023",
        notes = "Amazing trip! Visited Tokyo, Kyoto, and Osaka. The food was incredible.",
        rating = 5,
        flagEmoji = "\uD83C\uDDEF\uD83C\uDDF5",
    )
    val visitedNoNotes = CountryDetailUi(
        code = "FR",
        name = "France",
        region = "Europe",
        visited = true,
        visitedDate = 1_690_000_000_000L,
        visitedDateFormatted = "July 22, 2023",
        notes = "",
        rating = 3,
        flagEmoji = "\uD83C\uDDEB\uD83C\uDDF7",
    )
    val unvisited = CountryDetailUi(
        code = "BR",
        name = "Brazil",
        region = "Americas",
        visited = false,
        visitedDate = null,
        visitedDateFormatted = null,
        notes = "",
        rating = 0,
        flagEmoji = "\uD83C\uDDE7\uD83C\uDDF7",
    )
}

/**
 * [PreviewParameterProvider] for [CountryDetailUi].
 */
class CountryDetailUiPreviewProvider : PreviewParameterProvider<CountryDetailUi> {
    override val values = sequenceOf(
        PreviewCountryDetails.visitedWithNotes,
        PreviewCountryDetails.visitedNoNotes,
        PreviewCountryDetails.unvisited,
    )
}

/**
 * Sample region list for filter chip previews.
 */
val previewRegions = listOf("Africa", "Americas", "Asia", "Europe", "Oceania")
