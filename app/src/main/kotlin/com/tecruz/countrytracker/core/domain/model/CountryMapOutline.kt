package com.tecruz.countrytracker.core.domain.model

/**
 * One country's raw SVG outline, before it is turned into a draw path.
 *
 * @param code ISO country code used to match visited countries.
 * @param svgPath SVG path data in the world-map view box.
 */
data class CountryMapOutline(val code: String, val svgPath: String)
