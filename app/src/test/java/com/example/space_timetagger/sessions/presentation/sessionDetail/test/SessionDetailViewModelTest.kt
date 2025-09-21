package com.example.space_timetagger.sessions.presentation.sessionDetail.test

import assertk.assertThat
import assertk.assertions.extracting
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEmpty
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.example.space_timetagger.CoroutineTestRule
import com.example.space_timetagger.core.domain.repository.PreferencesRepository
import com.example.space_timetagger.location.domain.models.LatLng
import com.example.space_timetagger.location.domain.repository.LocationRepository
import com.example.space_timetagger.sessions.domain.mockDateTime
import com.example.space_timetagger.sessions.domain.mockSession
import com.example.space_timetagger.sessions.domain.mockTag
import com.example.space_timetagger.sessions.domain.models.SessionChange
import com.example.space_timetagger.sessions.domain.models.Tag
import com.example.space_timetagger.sessions.domain.repository.SessionsRepository
import com.example.space_timetagger.sessions.presentation.models.TagUiModel
import com.example.space_timetagger.sessions.presentation.sessionDetail.SessionDetailEvent
import com.example.space_timetagger.sessions.presentation.sessionDetail.SessionDetailViewState
import com.example.space_timetagger.sessions.presentation.sessionDetail.SessionViewModel
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
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class SessionDetailViewModelTest {
    private val validId = "existing-id"
    private val nonExistentId = "not-found-id"
    private val latLng = LatLng(49.00504, -123.00678)
    private val lastChange = SessionChange.AddTag(mockSession.tags.last().id)

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @Mock
    private val mockSessionsRepository = mock<SessionsRepository>()

    @Mock
    private val mockPreferencesRepository = mock<PreferencesRepository>()

    @Mock
    private val mockLocationRepository = mock<LocationRepository>()

    private lateinit var viewModel: SessionViewModel

    @Before
    fun setup() = runTest {
        whenever(mockSessionsRepository.sessionAndLastChange(validId)).thenReturn(
            flowOf(Pair(mockSession, lastChange))
        )
        whenever(mockSessionsRepository.sessionAndLastChange(nonExistentId)).thenReturn(flowOf(null))
        whenever(mockPreferencesRepository.taggingLocationIsEnabled).thenReturn(flowOf(true))
        whenever(mockPreferencesRepository.tapAnywhereIsEnabled).thenReturn(flowOf(false))
        whenever(mockLocationRepository.findCurrentLocation()).thenReturn(latLng)
    }

    private fun initViewModel(sessionId: String = validId) {
        viewModel = SessionViewModel(
            sessionId,
            mockSessionsRepository,
            mockPreferencesRepository,
            mockLocationRepository,
        )
    }

    // session doesn't exist - view state

    @Test
    fun nonExistentIdInitialState_producesErrorState() = runTest {
        initViewModel(nonExistentId)
        val initialViewState = viewModel.viewState.first()
        assertThat(initialViewState).hasClass<SessionDetailViewState.Error>()
    }

    // session exists - view state

    @Test
    fun initialState_isSuccessState() = runTest {
        initViewModel()
        val initialViewState = viewModel.viewState.first()
        assertThat(initialViewState).hasClass<SessionDetailViewState.Success>()
    }

    @Test
    fun initialSuccessState_hasNameFromRepository() = runTest {
        initViewModel()
        assertThat(session()::name).isEqualTo(mockSession.name)
    }

    @Test
    fun initialSuccessState_hasEditModeOff() = runTest {
        initViewModel()
        assertThat(session()::nameIsBeingEdited).isFalse()
    }

    @Test
    fun initialSuccessState_hasTagsFromRepository() = runTest {
        initViewModel()
        assertThat(session()::tags)
            .extracting(TagUiModel::id)
            .isEqualTo(mockSession.tags.map(Tag::id))
    }

    @Test
    fun initialSuccessState_hasTagIdToScrollToBasedOnRepository() = runTest {
        initViewModel()
        assertThat(session()::tagIdToScrollTo).isEqualTo(lastChange.id)
    }

    @Test
    fun initialSuccessState_hasDeleteAllButtonEnabled() = runTest {
        // test only applies if the mock data has tags
        assertThat(mockSession.tags).isNotEmpty()

        initViewModel()
        assertThat(session()::deleteAllIsEnabled).isTrue()
    }

    @Test
    fun initialSuccessState_tapAnywhereIsEnabledReflectsFalseFromRepository() = runTest {
        whenever(mockPreferencesRepository.tapAnywhereIsEnabled).thenReturn(flowOf(false))
        initViewModel()
        assertThat(session()::tapAnywhereIsEnabled).isFalse()
    }

    @Test
    fun initialSuccessState_tapAnywhereIsEnabledReflectsTrueFromRepository() = runTest {
        whenever(mockPreferencesRepository.tapAnywhereIsEnabled).thenReturn(flowOf(true))
        initViewModel()
        assertThat(session()::tapAnywhereIsEnabled).isTrue()
    }

    // FIXME: updating repository flows -> view state updates

    // session exists - handle events

    @Test
    fun eventTapEdit_turnsEditModeOn() = runTest {
        initViewModel()
        viewModel.handleEvent(SessionDetailEvent.TapEdit)
        assertThat(session()::nameIsBeingEdited).isTrue()
    }

    @Test
    fun eventCancelNameEdit_turnsEditModeOff() = runTest {
        initViewModel()
        viewModel.handleEvent(SessionDetailEvent.TapEdit)
        viewModel.handleEvent(SessionDetailEvent.CancelNameEdit)
        assertThat(session()::nameIsBeingEdited).isFalse()
    }

    @Test
    fun eventConfirmNameEdit_turnsEditModeOff() = runTest {
        initViewModel()
        viewModel.handleEvent(SessionDetailEvent.TapEdit)
        viewModel.handleEvent(SessionDetailEvent.ConfirmNameEdit("new dummy name"))
        assertThat(session()::nameIsBeingEdited).isFalse()
    }

    @Test
    fun eventConfirmNameEdit_callsRepositoryFunc() = runTest {
        initViewModel()
        val newName = "Updated Name String"
        viewModel.handleEvent(SessionDetailEvent.ConfirmNameEdit(newName))
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).renameSession(validId, newName)
    }

    @Test
    fun eventTapNewTagButtonWithLocationDisabled_callsRepositoryFunc() = runTest {
        whenever(mockPreferencesRepository.taggingLocationIsEnabled).thenReturn(flowOf(false))
        initViewModel()
        viewModel.handleEvent(SessionDetailEvent.TapNewTagButton(mockDateTime))
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).addTagToSession(
            eq(validId),
            argThat { tag -> tag.dateTime == mockDateTime && tag.location == null },
        )
    }

    @Test
    fun eventTapNewTagButtonWithLocationEnabled_callsRepositoryFunc() = runTest {
        whenever(mockPreferencesRepository.taggingLocationIsEnabled).thenReturn(flowOf(true))
        initViewModel()
        viewModel.handleEvent(SessionDetailEvent.TapNewTagButton(mockDateTime))
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).addTagToSession(
            eq(validId),
            argThat { tag -> tag.dateTime == mockDateTime && tag.location == latLng },
        )
    }

    @Test
    fun eventTapNewTagButtonWithLocationEnabledButFailedLocation_callsRepositoryFunc() = runTest {
        whenever(mockPreferencesRepository.taggingLocationIsEnabled).thenReturn(flowOf(true))
        whenever(mockLocationRepository.findCurrentLocation()).thenReturn(null)
        initViewModel()
        viewModel.handleEvent(SessionDetailEvent.TapNewTagButton(mockDateTime))
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).addTagToSession(
            eq(validId),
            argThat { tag -> tag.dateTime == mockDateTime && tag.location == null },
        )
    }

    @Test
    fun eventTapConfirmDeleteTag_callsRepositoryFunc() = runTest {
        initViewModel()
        viewModel.handleEvent(SessionDetailEvent.TapConfirmDeleteTag(mockTag.id))
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).removeTag(mockTag.id)
    }

    @Test
    fun eventTapConfirmDeleteAllTags_callsRepositoryFunc() = runTest {
        initViewModel()
        viewModel.handleEvent(SessionDetailEvent.TapConfirmDeleteAllTags)
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).removeAllTagsFromSession(validId)
    }

    @Test
    fun eventTapAnywhereWithTapAnywhereEnabled_callsRepositoryFunc() = runTest {
        whenever(mockPreferencesRepository.tapAnywhereIsEnabled).thenReturn(flowOf(true))
        initViewModel()
        viewModel.handleEvent(SessionDetailEvent.TapAnywhere(mockDateTime))
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).addTagToSession(
            eq(validId),
            any(),
        )
    }

    @Test
    fun eventTapAnywhereWithTapAnywhereDisabled_doesNotCallRepositoryFunc() = runTest {
        whenever(mockPreferencesRepository.tapAnywhereIsEnabled).thenReturn(flowOf(false))
        initViewModel()
        viewModel.handleEvent(SessionDetailEvent.TapAnywhere(mockDateTime))
        advanceUntilIdle()
        verify(mockSessionsRepository, never()).addTagToSession(
            any(),
            any(),
        )
    }

    @Test
    fun eventAutoScrollToTag_ClearsTagIdToScrollTo() = runTest {
        initViewModel()
        assertThat(session()::tagIdToScrollTo).isEqualTo(lastChange.id)

        viewModel.handleEvent(SessionDetailEvent.AutoScrollToTag(lastChange.id))
        advanceUntilIdle()
        assertThat(session()::tagIdToScrollTo).isNull()
    }

    // helpers

    private suspend fun session() =
        (viewModel.viewState.first() as SessionDetailViewState.Success).session
}
