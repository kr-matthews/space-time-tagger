package com.example.space_timetagger.sessions.presentation.settings

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isOff
import androidx.compose.ui.test.isOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.example.space_timetagger.R
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class SettingsViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    private val mockHandleEvent: (SettingsEvent) -> Unit = mock()

    private val successStateLocationEnabled = SettingsViewState.Success(true)
    private val successStateLocationDisabled = SettingsViewState.Success(false)
    private val successState = successStateLocationDisabled
    private val loadingState = SettingsViewState.Loading

    private fun setup(viewState: SettingsViewState) {
        composeTestRule.setContent {
            SpaceTimeTaggerTheme {
                SettingsView(viewState = viewState, onEvent = mockHandleEvent)
            }
        }
    }

    // success

    @Test
    fun successState_doesNotHaveProgressIndicator() {
        setup(successState)
        getProgressIndicator().assertDoesNotExist()
    }

    @Test
    fun successState_hasTitleText() {
        setup(successState)
        composeTestRule.onNodeWithText(appContext.getString(R.string.settings)).assertIsDisplayed()
    }

    @Test
    fun successState_doesNotHaveSettingsButton() {
        setup(successState)
        composeTestRule
            .onNode(hasContentDescription(appContext.getString(R.string.settings)))
            .assertDoesNotExist()
    }

    @Test
    fun successStateLocationEnabled_hasLocationSwitchOn() {
        setup(successStateLocationEnabled)
        getSwitch(appContext.getString(R.string.capture_location)).apply {
            assertIsDisplayed()
            isOn()
        }
    }

    @Test
    fun successStateLocationEnabled_hasLocationSwitchOff() {
        setup(successStateLocationDisabled)
        getSwitch(appContext.getString(R.string.capture_location)).apply {
            assertIsDisplayed()
            isOff()
        }
    }

    @Test
    fun successState_tappingSwitchCallsEventTapLocationTaggingToggle() {
        setup(successState)
        composeTestRule
            .onNodeWithText(appContext.getString(R.string.capture_location))
            .performClick()
        verify(mockHandleEvent, times(1)).invoke(SettingsEvent.TapLocationTaggingToggle)
    }

    @Test
    fun successState_tappingBackCallsEventTapBack() {
        setup(successState)
        composeTestRule
            .onNode(hasContentDescription(appContext.getString(R.string.back)))
            .performClick()
        verify(mockHandleEvent, times(1)).invoke(SettingsEvent.TapBack)
    }

    // loading

    @Test
    fun loadingState_hasProgressIndicator() {
        setup(loadingState)
        getProgressIndicator().assertIsDisplayed()
    }

    // helpers

    private fun getProgressIndicator() =
        composeTestRule.onNode(
            SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)
        )

    private fun getSwitch(label: String) =
        composeTestRule
            .onNodeWithText(label, useUnmergedTree = true)
            .onParent()
            .onChildren()
            .filterToOne(SemanticsMatcher.keyIsDefined(SemanticsProperties.ToggleableState))
}
