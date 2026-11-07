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
import com.tecruz.countrytracker.features.countrylist.presentation.components.ReadFailureNotice
import com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.WorldMapCanvas
import com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.WorldMapCanvasPreview
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
    fun `map read failure without a message uses the fallback and can be retried`() {
        var retries = 0
        setScreen(
            state = CountryListState(
                isLoading = false,
                visitedCodesStatus = DataReadStatus.Ready,
                statisticsStatus = DataReadStatus.Ready,
            ),
            mapState = WorldMapState(status = DataReadStatus.Unavailable, error = null),
            onAction = { if (it is CountryListAction.OnRetry) retries += 1 },
        )

        composeTestRule.onNodeWithText("Map").performClick()
        composeTestRule.onNodeWithText("Could not load the world map").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").performClick()
        assertEquals(1, retries)
    }

    @Test
    fun `map read failure shows the loader error instead of an empty map`() {
        setScreen(
            state = CountryListState(
                isLoading = false,
                visitedCodesStatus = DataReadStatus.Ready,
                statisticsStatus = DataReadStatus.Ready,
            ),
            mapState = WorldMapState(
                status = DataReadStatus.Unavailable,
                error = UiText.DynamicString("shapes failed"),
            ),
        )

        composeTestRule.onNodeWithText("Map").performClick()
        composeTestRule.onNodeWithText("shapes failed").assertIsDisplayed()
        composeTestRule.onNodeWithTag("world_map").assertDoesNotExist()
    }

    @Test
    fun `region read failure offers retry instead of an empty region list`() {
        var retries = 0
        setScreen(
            state = CountryListState(
                isLoading = false,
                countriesStatus = DataReadStatus.Ready,
                regionsStatus = DataReadStatus.Unavailable,
                countries = emptyList(),
            ),
            onAction = { if (it is CountryListAction.OnRetry) retries += 1 },
        )

        composeTestRule.onNodeWithText("Could not load regions").assertIsDisplayed()
        composeTestRule.onNodeWithText("No countries found").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").performClick()
        assertEquals(1, retries)
    }

    @Test
    fun `failure notice uses its default modifier`() {
        var retried = false
        composeTestRule.setContent {
            CountryTrackerTheme {
                ReadFailureNotice(
                    message = UiText.DynamicString("Could not load regions"),
                    onRetry = { retried = true },
                )
            }
        }

        composeTestRule.onNodeWithText("Could not load regions").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").performClick()
        assertEquals(true, retried)
    }

    @Test
    fun `statistics stay hidden while the country list is still loading`() {
        setScreen(
            CountryListState(
                isLoading = true,
                statisticsStatus = DataReadStatus.Loading,
                visitedCount = 0,
                totalCount = 0,
            ),
        )

        composeTestRule.onNodeWithText("Map").performClick()
        composeTestRule.onNodeWithText("VISITED").assertDoesNotExist()
    }

    @Test
    fun `map stays hidden while visited codes are still loading`() {
        setScreen(
            state = CountryListState(
                isLoading = false,
                visitedCodesStatus = DataReadStatus.Loading,
                statisticsStatus = DataReadStatus.Ready,
            ),
            mapState = WorldMapState(status = DataReadStatus.Ready),
        )

        composeTestRule.onNodeWithText("Map").performClick()
        composeTestRule.onNodeWithText("VISITED").assertExists()
        composeTestRule.onNodeWithTag("world_map").assertDoesNotExist()
    }

    @Test
    fun `map stays hidden while geometry is still loading`() {
        setScreen(
            state = CountryListState(
                isLoading = false,
                visitedCodesStatus = DataReadStatus.Ready,
                statisticsStatus = DataReadStatus.Ready,
            ),
            mapState = WorldMapState(status = DataReadStatus.Loading),
        )

        composeTestRule.onNodeWithText("Map").performClick()
        composeTestRule.onNodeWithText("VISITED").assertExists()
        composeTestRule.onNodeWithTag("world_map").assertDoesNotExist()
    }

    @Test
    fun `preview draws the sample path`() {
        composeTestRule.setContent {
            WorldMapCanvasPreview()
        }

        composeTestRule.onNodeWithTag("world_map").assertIsDisplayed()
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
