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
import org.mockito.kotlin.argThat
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class SettingsViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    private val mockHandleEvent: (SettingsEvent) -> Unit = mock()

    private val enabledState = SettingsViewState.Success(
        keepScreenOnIsEnabled = true,
        taggingLocationIsEnabled = true,
        tapAnywhereIsEnabled = true,
    )
    private val disabledState = SettingsViewState.Success(
        keepScreenOnIsEnabled = false,
        taggingLocationIsEnabled = false,
        tapAnywhereIsEnabled = false
    )
    private val mixedState = SettingsViewState.Success(
        keepScreenOnIsEnabled = true,
        taggingLocationIsEnabled = false,
        tapAnywhereIsEnabled = true,
    )
    private val successState = mixedState
    private val requestLaunchState = SettingsViewState.Success(
        keepScreenOnIsEnabled = true,
        taggingLocationIsEnabled = false,
        tapAnywhereIsEnabled = false,
        locationPermissionMustBeRequested = true,
    )
    private val permissionDeniedState = SettingsViewState.Success(
        keepScreenOnIsEnabled = false,
        taggingLocationIsEnabled = false,
        tapAnywhereIsEnabled = true,
        locationPermissionExplanationIsVisible = true,
    )
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
    fun successState_doesNotHavePermissionExplanation() {
        setup(successState)
        composeTestRule
            .onNodeWithText(appContext.getString(R.string.why_location_permission_is_required))
            .assertDoesNotExist()
    }

    @Test
    fun successState_doesNotCallEventLocationPermissionRequestLaunched() {
        setup(successState)
        verify(mockHandleEvent, never()).invoke(SettingsEvent.LocationPermissionRequestLaunched)
    }

    @Test
    fun successState_doesNotHaveSettingsButton() {
        setup(successState)
        composeTestRule
            .onNode(hasContentDescription(appContext.getString(R.string.settings)))
            .assertDoesNotExist()
    }

    @Test
    fun successState_tappingBackCallsEventTapBack() {
        setup(successState)
        composeTestRule
            .onNode(hasContentDescription(appContext.getString(R.string.back)))
            .performClick()
        verify(mockHandleEvent, times(1)).invoke(SettingsEvent.TapBack)
    }

    // not sure how to mock/check hasLocationPermission is passed correctly
    @Test
    fun successState_tappingSwitchCallsEventTapLocationTaggingToggle() {
        setup(successState)
        composeTestRule
            .onNodeWithText(appContext.getString(R.string.capture_location))
            .performClick()
        verify(mockHandleEvent, times(1)).invoke(
            argThat { this is SettingsEvent.TapLocationTaggingToggle }
//            SettingsEvent.TapLocationTaggingToggle(hasLocationPermission = any())
        )
    }

    // switch states

    @Test
    fun enabledState_hasLocationSwitchOn() {
        setup(enabledState)
        getSwitch(appContext.getString(R.string.capture_location)).apply {
            assertIsDisplayed()
            isOn()
        }
    }

    @Test
    fun disabledState_hasLocationSwitchOff() {
        setup(disabledState)
        getSwitch(appContext.getString(R.string.capture_location)).apply {
            assertIsDisplayed()
            isOff()
        }
    }

    // request launch

    // not sure how to check request was actually launched
    @Test
    fun requestLaunchState_callsEventLocationPermissionRequestLaunched() {
        setup(requestLaunchState)
        verify(mockHandleEvent, times(1)).invoke(SettingsEvent.LocationPermissionRequestLaunched)
    }

    // permission denied

    @Test
    fun permissionDeniedState_hasExplanationDialog() {
        setup(permissionDeniedState)
        composeTestRule
            .onNodeWithText(appContext.getString(R.string.why_location_permission_is_required))
            .assertIsDisplayed()
    }

    @Test
    fun dismissingExplanationDialog_callsEventLocationPermissionDialogDismissed() {
        setup(permissionDeniedState)
        composeTestRule
            .onNodeWithText(appContext.getString(R.string.ok))
            .performClick()
        verify(mockHandleEvent, times(1)).invoke(SettingsEvent.LocationPermissionDialogDismissed)
    }

    // loading

    @Test
    fun loadingState_hasProgressIndicator() {
        setup(loadingState)
        getProgressIndicator().assertIsDisplayed()
    }

    @Test
    fun loadingState_hasTitleText() {
        setup(loadingState)
        composeTestRule.onNodeWithText(appContext.getString(R.string.settings)).assertIsDisplayed()
    }

    @Test
    fun loadingState_doesNotHavePermissionExplanation() {
        setup(loadingState)
        composeTestRule
            .onNodeWithText(appContext.getString(R.string.why_location_permission_is_required))
            .assertDoesNotExist()
    }

    @Test
    fun loadingState_doesNotCallEventLocationPermissionRequestLaunched() {
        setup(loadingState)
        verify(mockHandleEvent, never()).invoke(SettingsEvent.LocationPermissionRequestLaunched)
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
