package com.tecruz.countrytracker.features.countrylist.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import com.tecruz.countrytracker.LocalWindowSizeClass
import com.tecruz.countrytracker.core.designsystem.CountryTrackerTheme
import com.tecruz.countrytracker.core.presentation.UiText
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryListItem
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented coverage for the stateful parts of [CountryListScreen].
 *
 * The end-to-end test only walks one state on a compact device. These tests
 * render the stateless screen directly so every branch (loading, empty, list,
 * multi-column grid, map tab and snackbar host) is exercised on-device.
 */
@RunWith(AndroidJUnit4::class)
class CountryListScreenStatesInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val compactWindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 375f, heightDp = 667f)
    private val mediumWindowSizeClass =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp = 700f, heightDp = 900f)

    private val countries = listOf(
        CountryListItem(
            code = "BR",
            name = "Brazil",
            region = "South America",
            visited = true,
            flagEmoji = "\uD83C\uDDE7\uD83C\uDDF7",
        ),
        CountryListItem(
            code = "FR",
            name = "France",
            region = "Europe",
            visited = false,
            flagEmoji = "\uD83C\uDDEB\uD83C\uDDF7",
        ),
    )

    private fun setScreen(state: CountryListState, windowSizeClass: WindowSizeClass = compactWindowSizeClass) {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
                    CountryListScreen(state = state, onAction = {})
                }
            }
        }
    }

    @Test
    fun loadingState_showsSearchAndFilters() {
        setScreen(CountryListState(isLoading = true))

        composeTestRule.onNodeWithText("Countries").assertIsDisplayed()
        composeTestRule.onNodeWithText("Map").assertIsDisplayed()
    }

    @Test
    fun emptyState_showsNoCountriesMessage() {
        setScreen(CountryListState(isLoading = false, countries = emptyList()))

        composeTestRule.onNodeWithText("No countries found").assertIsDisplayed()
    }

    @Test
    fun compactState_rendersSingleColumnList() {
        setScreen(CountryListState(isLoading = false, countries = countries, totalCount = 195))

        composeTestRule.onNodeWithText("Brazil").assertIsDisplayed()
        composeTestRule.onNodeWithText("France").assertIsDisplayed()
    }

    @Test
    fun mediumState_rendersMultiColumnGrid() {
        setScreen(
            state = CountryListState(isLoading = false, countries = countries, totalCount = 195),
            windowSizeClass = mediumWindowSizeClass,
        )

        composeTestRule.onNodeWithText("Brazil").assertIsDisplayed()
        composeTestRule.onNodeWithText("France").assertIsDisplayed()
    }

    @Test
    fun mapTab_rendersStatisticsAndMap() {
        setScreen(CountryListState(isLoading = false, countries = countries, totalCount = 195, visitedCount = 1))

        composeTestRule.onNodeWithText("Map").performClick()

        composeTestRule.onNodeWithText("VISITED", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("TOTAL", substring = true).assertIsDisplayed()
    }

    @Test
    fun countriesTab_isDefaultAndSelectable() {
        setScreen(CountryListState(isLoading = false, countries = countries))

        composeTestRule.onNodeWithText("Map").performClick()
        composeTestRule.onNodeWithText("Countries").performClick()

        composeTestRule.onNodeWithText("Brazil").assertIsDisplayed()
    }

    @Test
    fun snackbarHost_rendersSnackbarWhenShown() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindowSizeClass) {
                    val snackbarHostState = remember { SnackbarHostState() }
                    LaunchedEffect(Unit) {
                        snackbarHostState.showSnackbar("Something failed")
                    }
                    CountryListScreen(
                        state = CountryListState(isLoading = false, error = UiText.DynamicString("Something failed")),
                        onAction = {},
                        snackbarHostState = snackbarHostState,
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Something failed").assertIsDisplayed()
    }
}
