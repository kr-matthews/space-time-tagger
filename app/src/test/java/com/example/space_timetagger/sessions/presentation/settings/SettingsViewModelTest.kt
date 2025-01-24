package com.example.space_timetagger.sessions.presentation.settings

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.example.space_timetagger.CoroutineTestRule
import com.example.space_timetagger.core.domain.repository.PreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class SettingsViewModelTest {

    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @Mock
    private val mockPreferencesRepository = mock<PreferencesRepository>()

    private lateinit var viewModel: SettingsViewModel

    private fun initViewModel() {
        viewModel = SettingsViewModel(mockPreferencesRepository)
    }

    @Before
    fun setup() {
        whenever(mockPreferencesRepository.taggingLocationIsEnabled).thenReturn(flowOf(true))
        whenever(mockPreferencesRepository.keepScreenOnIsEnabled).thenReturn(flowOf(true))
    }

    @Test
    fun initialState_taggingDisabledIsReflected() = runTest {
        whenever(mockPreferencesRepository.taggingLocationIsEnabled).thenReturn(flowOf(false))
        initViewModel()
        assertThat(viewModel.viewState.first()::taggingLocationIsEnabled).isFalse()
    }

    @Test
    fun initialState_taggingEnabledIsReflected() = runTest {
        whenever(mockPreferencesRepository.taggingLocationIsEnabled).thenReturn(flowOf(true))
        initViewModel()
        assertThat(viewModel.viewState.first()::taggingLocationIsEnabled).isTrue()
    }

    @Test
    fun initialState_locationPermissionStatesAreFalse() = runTest {
        initViewModel()
        assertThat(viewModel.viewState.first()::locationPermissionMustBeRequested).isFalse()
        assertThat(viewModel.viewState.first()::locationPermissionExplanationIsVisible).isFalse()
    }

    // FIXME: test that if repository flow updates, view state will update

    @Test
    fun withLocationDisabledEventTapLocationTaggingToggleWithoutPermission_promptsLocationPermissionRequest() =
        runTest {
            whenever(mockPreferencesRepository.taggingLocationIsEnabled).thenReturn(flowOf(false))
            initViewModel()
            viewModel.handleEvent(SettingsEvent.TapLocationTaggingToggle(false))
            advanceUntilIdle()
            verify(mockPreferencesRepository, never()).enableTaggingLocation()
            assertThat(viewModel.viewState.first()::locationPermissionMustBeRequested).isTrue()
            assertThat(viewModel.viewState.first()::locationPermissionExplanationIsVisible).isFalse()
        }

    @Test
    fun withLocationDisabledEventTapLocationTaggingToggleWithPermission_callsRepositoryFunction() =
        runTest {
            whenever(mockPreferencesRepository.taggingLocationIsEnabled).thenReturn(flowOf(false))
            initViewModel()
            viewModel.handleEvent(SettingsEvent.TapLocationTaggingToggle(true))
            advanceUntilIdle()
            verify(mockPreferencesRepository, times(1)).enableTaggingLocation()
            assertThat(viewModel.viewState.first()::locationPermissionMustBeRequested).isFalse()
            assertThat(viewModel.viewState.first()::locationPermissionExplanationIsVisible).isFalse()
        }

    @Test
    fun withLocationEnabledEventTapLocationTaggingToggleWithoutPermission_callsRepositoryFunction() =
        runTest {
            whenever(mockPreferencesRepository.taggingLocationIsEnabled).thenReturn(flowOf(true))
            initViewModel()
            viewModel.handleEvent(SettingsEvent.TapLocationTaggingToggle(false))
            advanceUntilIdle()
            verify(mockPreferencesRepository, times(1)).disableTaggingLocation()
            assertThat(viewModel.viewState.first()::locationPermissionMustBeRequested).isFalse()
            assertThat(viewModel.viewState.first()::locationPermissionExplanationIsVisible).isFalse()
        }

    @Test
    fun withLocationEnabledEventTapLocationTaggingToggleWithPermission_callsRepositoryFunction() =
        runTest {
            whenever(mockPreferencesRepository.taggingLocationIsEnabled).thenReturn(flowOf(true))
            initViewModel()
            viewModel.handleEvent(SettingsEvent.TapLocationTaggingToggle(true))
            advanceUntilIdle()
            verify(mockPreferencesRepository, times(1)).disableTaggingLocation()
            assertThat(viewModel.viewState.first()::locationPermissionMustBeRequested).isFalse()
            assertThat(viewModel.viewState.first()::locationPermissionExplanationIsVisible).isFalse()
        }

    @Test
    fun eventLocationPermissionRequestLaunched_resetsRequestState() = runTest {
        whenever(mockPreferencesRepository.taggingLocationIsEnabled).thenReturn(flowOf(false))
        initViewModel()
        viewModel.handleEvent(SettingsEvent.TapLocationTaggingToggle(false))
        advanceUntilIdle()
        viewModel.handleEvent(SettingsEvent.LocationPermissionRequestLaunched)
        advanceUntilIdle()
        assertThat(viewModel.viewState.first()::locationPermissionMustBeRequested).isFalse()
        assertThat(viewModel.viewState.first()::locationPermissionExplanationIsVisible).isFalse()
    }

    @Test
    fun eventLocationPermissionGranted_callsRepositoryFunction() = runTest {
        whenever(mockPreferencesRepository.taggingLocationIsEnabled).thenReturn(flowOf(false))
        initViewModel()
        viewModel.handleEvent(SettingsEvent.TapLocationTaggingToggle(false))
        advanceUntilIdle()
        viewModel.handleEvent(SettingsEvent.LocationPermissionRequestLaunched)
        advanceUntilIdle()
        viewModel.handleEvent(SettingsEvent.LocationPermissionGranted)
        advanceUntilIdle()
        verify(mockPreferencesRepository, times(1)).enableTaggingLocation()
        assertThat(viewModel.viewState.first()::locationPermissionMustBeRequested).isFalse()
        assertThat(viewModel.viewState.first()::locationPermissionExplanationIsVisible).isFalse()
    }

    @Test
    fun eventLocationPermissionDenied_showsExplanation() = runTest {
        whenever(mockPreferencesRepository.taggingLocationIsEnabled).thenReturn(flowOf(false))
        initViewModel()
        viewModel.handleEvent(SettingsEvent.TapLocationTaggingToggle(false))
        advanceUntilIdle()
        viewModel.handleEvent(SettingsEvent.LocationPermissionRequestLaunched)
        advanceUntilIdle()
        viewModel.handleEvent(SettingsEvent.LocationPermissionDenied)
        advanceUntilIdle()
        verify(mockPreferencesRepository, never()).enableTaggingLocation()
        assertThat(viewModel.viewState.first()::locationPermissionMustBeRequested).isFalse()
        assertThat(viewModel.viewState.first()::locationPermissionExplanationIsVisible).isTrue()
    }

    @Test
    fun eventLocationPermissionDialogDismissed_clearsExplanation() = runTest {
        whenever(mockPreferencesRepository.taggingLocationIsEnabled).thenReturn(flowOf(false))
        initViewModel()
        viewModel.handleEvent(SettingsEvent.TapLocationTaggingToggle(false))
        advanceUntilIdle()
        viewModel.handleEvent(SettingsEvent.LocationPermissionRequestLaunched)
        advanceUntilIdle()
        viewModel.handleEvent(SettingsEvent.LocationPermissionDenied)
        advanceUntilIdle()
        viewModel.handleEvent(SettingsEvent.LocationPermissionDialogDismissed)
        advanceUntilIdle()
        verify(mockPreferencesRepository, never()).enableTaggingLocation()
        assertThat(viewModel.viewState.first()::locationPermissionMustBeRequested).isFalse()
        assertThat(viewModel.viewState.first()::locationPermissionExplanationIsVisible).isFalse()
    }
}
