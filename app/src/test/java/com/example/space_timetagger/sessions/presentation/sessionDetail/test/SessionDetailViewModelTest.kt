package com.example.space_timetagger.sessions.presentation.sessionDetail.test

import com.example.space_timetagger.CoroutineTestRule
import com.example.space_timetagger.sessions.domain.mockDateTime
import com.example.space_timetagger.sessions.domain.mockSession
import com.example.space_timetagger.sessions.domain.mockTag
import com.example.space_timetagger.sessions.domain.repository.SessionsRepository
import com.example.space_timetagger.sessions.presentation.sessionDetail.SessionDetailEvent
import com.example.space_timetagger.sessions.presentation.sessionDetail.SessionDetailViewState
import com.example.space_timetagger.sessions.presentation.sessionDetail.SessionViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argThat
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class SessionDetailViewModelTest {
    private val validId = "existing-id"
    private val nonExistentId = "not-found-id"

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @Mock
    private val mockSessionsRepository = mock<SessionsRepository>()

    private lateinit var viewModel: SessionViewModel
    private lateinit var viewModelNonExistentSession: SessionViewModel

    @Before
    fun setup() = runTest {
        whenever(mockSessionsRepository.session(validId)).thenReturn(flowOf(mockSession))
        whenever(mockSessionsRepository.session(nonExistentId)).thenReturn(flowOf(null))
        viewModel = SessionViewModel(validId, mockSessionsRepository)
        viewModelNonExistentSession = SessionViewModel(nonExistentId, mockSessionsRepository)
    }

    // session doesn't exist - view state

    @Test
    fun nonExistentIdInitialState_producesErrorState() = runTest {
        val initialViewState = viewModelNonExistentSession.viewState.first()
        assert(initialViewState is SessionDetailViewState.Error)
    }

    // session exists - view state

    @Test
    fun initialState_isSuccessState() = runTest {
        val initialViewState = viewModel.viewState.first()
        assert(initialViewState is SessionDetailViewState.Success)
    }

    @Test
    fun initialSuccessState_hasNameFromRepository() = runTest {
        assertEquals(mockSession.name, getSuccessViewState().session.name)
    }

    @Test
    fun initialSuccessState_hasEditModeOff() = runTest {
        assert(!getSuccessViewState().session.nameIsBeingEdited)
    }

    @Test
    fun initialSuccessState_hasTagsFromRepository() = runTest {
        assertEquals(
            mockSession.tags.map { it.id },
            getSuccessViewState().session.tags.map { it.id })
    }

    @Test
    fun initialSuccessState_hasDeleteAllButtonEnabled() = runTest {
        // only true if the session has tags
        assert(mockSession.tags.isNotEmpty())
        assert(getSuccessViewState().session.deleteAllIsEnabled)
    }

    // TODO: updating repository flows -> view state updates

    // session exists - handle events

    @Test
    fun eventTapName_turnsEditModeOn() = runTest {
        viewModel.handleEvent(SessionDetailEvent.TapName)
        assert(getSuccessViewState().session.nameIsBeingEdited)
    }

    @Test
    fun eventTapNameDoneEditing_turnsEditModeOff() = runTest {
        viewModel.handleEvent(SessionDetailEvent.TapName)
        viewModel.handleEvent(SessionDetailEvent.TapNameDoneEditing)
        assert(!getSuccessViewState().session.nameIsBeingEdited)
    }

    @Test
    fun eventChangeName_callsRepositoryFunc() = runTest {
        val newName = "Updated Name String"
        viewModel.handleEvent(SessionDetailEvent.ChangeName(newName))
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).renameSession(validId, newName)
    }

    @Test
    fun eventTapNewTagButton_callsRepositoryFunc() = runTest {
        viewModel.handleEvent(SessionDetailEvent.TapNewTagButton(mockDateTime))
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).addTagToSession(
            eq(validId),
            argThat { tag -> tag.dateTime == mockDateTime },
        )
    }

    @Test
    fun eventTapConfirmDeleteTag_callsRepositoryFunc() = runTest {
        viewModel.handleEvent(SessionDetailEvent.TapConfirmDeleteTag(mockTag.id))
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).removeTagFromSession(validId, mockTag.id)
    }

    @Test
    fun eventTapConfirmDeleteAllTags_callsRepositoryFunc() = runTest {
        viewModel.handleEvent(SessionDetailEvent.TapConfirmDeleteAllTags)
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).removeAllTagsFromSession(validId)
    }

    // helpers

    private suspend fun getSuccessViewState() =
        viewModel.viewState.first() as SessionDetailViewState.Success
}
