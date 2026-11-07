package com.tecruz.countrytracker.features.countrylist.presentation

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import com.tecruz.countrytracker.LocalWindowSizeClass
import com.tecruz.countrytracker.core.data.datasource.WorldMapPathData
import com.tecruz.countrytracker.core.designsystem.CountryTrackerTheme
import com.tecruz.countrytracker.core.presentation.UiText
import com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.WorldMapCanvas
import com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.model.CountryPathData
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CountryListErrorUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @After
    fun tearDown() {
        try {
            stopKoin()
        } catch (_: IllegalStateException) {
            // Robolectric may have started Koin from the application.
        }
    }

    private val compactWindow = WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(
        widthDp = 375f,
        heightDp = 667f,
    )

    @Test
    fun `failed country read is not an empty success and retry is offered`() {
        var retried = false
        setScreen(
            CountryListState(
                isLoading = false,
                countriesStatus = DataReadStatus.Unavailable,
                error = UiText.DynamicString("Failed to load countries"),
            ),
        ) {
            if (it is CountryListAction.OnRetry) {
                retried = true
            }
        }

        composeTestRule.onNodeWithText("No countries found").assertDoesNotExist()
        composeTestRule.onNodeWithText("Failed to load countries").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").performClick()
        assertEquals(true, retried)
    }

    @Test
    fun `ready empty country list is the empty state`() {
        setScreen(
            CountryListState(
                isLoading = false,
                countriesStatus = DataReadStatus.Ready,
            ),
        )

        composeTestRule.onNodeWithText("No countries found").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertDoesNotExist()
    }

    @Test
    fun `failed statistics are not shown as zero visited countries`() {
        setScreen(
            CountryListState(
                isLoading = false,
                statisticsStatus = DataReadStatus.Unavailable,
                visitedCodesStatus = DataReadStatus.Ready,
                visitedCount = 0,
                totalCount = 0,
                percentage = 0,
            ),
            mapState = WorldMapState(status = DataReadStatus.Ready),
        )

        composeTestRule.onNodeWithText("Map").performClick()
        composeTestRule.onNodeWithText("Could not load visit statistics").assertIsDisplayed()
        composeTestRule.onNodeWithText("VISITED").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("World map with no visited countries").assertIsDisplayed()
    }

    @Test
    fun `failed visited codes are not drawn as an unvisited map`() {
        setScreen(
            CountryListState(
                isLoading = false,
                visitedCodesStatus = DataReadStatus.Unavailable,
                statisticsStatus = DataReadStatus.Ready,
                visitedCount = 0,
            ),
            mapState = WorldMapState(status = DataReadStatus.Ready),
        )

        composeTestRule.onNodeWithText("Map").performClick()
        composeTestRule.onNodeWithText("Could not load visited countries").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("World map with no visited countries").assertDoesNotExist()
        composeTestRule.onNodeWithTag("world_map").assertDoesNotExist()
    }

    @Test
    fun `legitimate zero visited countries still render the map`() {
        setScreen(
            CountryListState(
                isLoading = false,
                countriesStatus = DataReadStatus.Ready,
                statisticsStatus = DataReadStatus.Ready,
                visitedCodesStatus = DataReadStatus.Ready,
                regionsStatus = DataReadStatus.Ready,
                visitedCount = 0,
                totalCount = 4,
                percentage = 0,
            ),
            mapState = WorldMapState(status = DataReadStatus.Ready),
        )

        composeTestRule.onNodeWithText("Map").performClick()
        composeTestRule.onNodeWithText("VISITED").assertExists()
        composeTestRule.onNodeWithContentDescription("World map with no visited countries").assertIsDisplayed()
    }

    @Test
    fun `world map composition does not read map assets`() {
        WorldMapPathData.reset()
        val bounds = Rect(0f, 0f, 10f, 10f)
        val path = Path().apply { addRect(bounds) }

        composeTestRule.setContent {
            CountryTrackerTheme {
                WorldMapCanvas(
                    visitedCountryCodes = setOf("US"),
                    countryPaths = listOf(CountryPathData("US", path, bounds)),
                    modifier = Modifier.size(width = 320.dp, height = 180.dp),
                )
            }
        }

        composeTestRule.onNodeWithTag("world_map").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("US", substring = true).assertIsDisplayed()
        assertFalse(WorldMapPathData.isLoaded)
    }

    private fun setScreen(
        state: CountryListState,
        mapState: WorldMapState = WorldMapState(),
        onAction: (CountryListAction) -> Unit = {},
    ) {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(LocalWindowSizeClass provides compactWindow) {
                    CountryListScreen(
                        state = state,
                        onAction = onAction,
                        mapState = mapState,
                    )
                }
            }
        }
    }
}
