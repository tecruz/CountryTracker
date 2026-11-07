package com.tecruz.countrytracker.features.countrylist.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import com.tecruz.countrytracker.LocalWindowSizeClass
import com.tecruz.countrytracker.R
import com.tecruz.countrytracker.core.designsystem.CountryTrackerTheme
import com.tecruz.countrytracker.core.presentation.UiText
import com.tecruz.countrytracker.features.countrylist.presentation.di.countryListPresentationModule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CountryListRootEventTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val state = MutableStateFlow(CountryListState(isLoading = false))
    private val mapState = MutableStateFlow(WorldMapState(status = DataReadStatus.Ready))
    private val events = MutableSharedFlow<CountryListEvent>(extraBufferCapacity = 4)
    private val fakeViewModel = mockk<CountryListViewModel>(relaxed = true)
    private val testModule = module {
        viewModel { fakeViewModel }
    }
    private val compactWindow = WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(
        widthDp = 375f,
        heightDp = 667f,
    )
    private val viewModelOwner = object : ViewModelStoreOwner {
        override val viewModelStore = ViewModelStore()
    }

    @Before
    fun setUp() {
        every { fakeViewModel.state } returns state
        every { fakeViewModel.mapState } returns mapState
        every { fakeViewModel.events } returns events
        unloadKoinModules(countryListPresentationModule)
        loadKoinModules(testModule)
    }

    @After
    fun tearDown() {
        try {
            stopKoin()
        } catch (_: IllegalStateException) {
            // The application may not have started Koin in this test.
        }
    }

    @Test
    fun `a state error is resolved into the snackbar`() {
        state.value = CountryListState(
            isLoading = false,
            error = UiText.StringResource(R.string.notes_character_count, arrayOf(1, 4)),
        )

        showRoot()

        composeTestRule.onNodeWithText("1 / 4").assertExists()
    }

    @Test
    fun `a map error is resolved into the snackbar`() {
        mapState.value = WorldMapState(
            status = DataReadStatus.Unavailable,
            error = UiText.DynamicString("map failed"),
        )

        showRoot()

        composeTestRule.onNodeWithText("map failed").assertExists()
    }

    @Test
    fun `snackbar events resolve text and ignore an empty message`() {
        showRoot()

        events.tryEmit(CountryListEvent.ShowSnackbar(UiText.DynamicString("")))
        composeTestRule.waitForIdle()
        events.tryEmit(CountryListEvent.ShowSnackbar(UiText.StringResource(R.string.error_load_map)))
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Could not load the world map").assertExists()
    }

    private fun showRoot() {
        composeTestRule.setContent {
            CountryTrackerTheme {
                CompositionLocalProvider(
                    LocalWindowSizeClass provides compactWindow,
                    LocalViewModelStoreOwner provides viewModelOwner,
                ) {
                    CountryListRoot(
                        onCountryClick = {},
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
    }
}
