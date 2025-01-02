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
    }

    @Test
    fun initialState_whenRepositoryReturnsFalseStateIsFalse() = runTest {
        whenever(mockPreferencesRepository.taggingLocationIsEnabled).thenReturn(flowOf(false))
        initViewModel()
        val initialViewState = viewModel.viewState.first()
        assertThat(initialViewState::taggingLocationIsEnabled).isFalse()
    }

    @Test
    fun initialState_whenRepositoryReturnsTrueStateIsTrue() = runTest {
        whenever(mockPreferencesRepository.taggingLocationIsEnabled).thenReturn(flowOf(true))
        initViewModel()
        val initialViewState = viewModel.viewState.first()
        assertThat(initialViewState::taggingLocationIsEnabled).isTrue()
    }

    // FIXME: test that if repository flow updates, view state will update

    @Test
    fun eventEnableLocationTagging_callsRepositoryFunction() = runTest {
        initViewModel()
        viewModel.handleEvent(SettingsEvent.EnableLocationTagging)
        advanceUntilIdle()
        verify(mockPreferencesRepository, times(1)).enableTaggingLocation()
    }

    @Test
    fun eventDisableLocationTagging_callsRepositoryFunction() = runTest {
        initViewModel()
        viewModel.handleEvent(SettingsEvent.DisableLocationTagging)
        advanceUntilIdle()
        verify(mockPreferencesRepository, times(1)).disableTaggingLocation()
    }
}
