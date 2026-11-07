package com.tecruz.countrytracker.features.countrylist.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.WorldMapCanvasPreview
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WorldMapCanvasPreviewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun previewRendersSuppliedPathWithoutReadingAssets() {
        composeTestRule.setContent {
            WorldMapCanvasPreview()
        }

        composeTestRule.onNodeWithTag("world_map").assertIsDisplayed()
    }
}
