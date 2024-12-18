package com.example.space_timetagger.sessions.presentation.sessionDetail.test

import assertk.assertThat
import assertk.assertions.extracting
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEmpty
import assertk.assertions.isTrue
import com.example.space_timetagger.CoroutineTestRule
import com.example.space_timetagger.sessions.domain.mockDateTime
import com.example.space_timetagger.sessions.domain.mockSession
import com.example.space_timetagger.sessions.domain.mockTag
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
        assertThat(initialViewState).hasClass<SessionDetailViewState.Error>()
    }

    // session exists - view state

    @Test
    fun initialState_isSuccessState() = runTest {
        val initialViewState = viewModel.viewState.first()
        assertThat(initialViewState).hasClass<SessionDetailViewState.Success>()
    }

    @Test
    fun initialSuccessState_hasNameFromRepository() = runTest {
        assertThat(session()::name).isEqualTo(mockSession.name)
    }

    @Test
    fun initialSuccessState_hasEditModeOff() = runTest {
        assertThat(session()::nameIsBeingEdited).isFalse()
    }

    @Test
    fun initialSuccessState_hasTagsFromRepository() = runTest {
        assertThat(session()::tags)
            .extracting(TagUiModel::id)
            .isEqualTo(mockSession.tags.map(Tag::id))
    }

    @Test
    fun initialSuccessState_hasDeleteAllButtonEnabled() = runTest {
        // test only applies if the mock data has tags
        assertThat(mockSession.tags).isNotEmpty()
        assertThat(session()::deleteAllIsEnabled).isTrue()
    }

    // FIXME: updating repository flows -> view state updates

    // session exists - handle events

    @Test
    fun eventTapName_turnsEditModeOn() = runTest {
        viewModel.handleEvent(SessionDetailEvent.TapName)
        assertThat(session()::nameIsBeingEdited).isTrue()
    }

    @Test
    fun eventTapNameDoneEditing_turnsEditModeOff() = runTest {
        viewModel.handleEvent(SessionDetailEvent.TapName)
        viewModel.handleEvent(SessionDetailEvent.TapNameDoneEditing)
        assertThat(session()::nameIsBeingEdited).isFalse()
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

    private suspend fun session() =
        (viewModel.viewState.first() as SessionDetailViewState.Success).session
}
