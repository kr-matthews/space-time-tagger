package com.example.space_timetagger.sessions.presentation.sessionsList.test

import assertk.assertThat
import assertk.assertions.extracting
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.example.space_timetagger.CoroutineTestRule
import com.example.space_timetagger.sessions.domain.mockSessions
import com.example.space_timetagger.sessions.domain.models.Session
import com.example.space_timetagger.sessions.domain.models.SessionsChange
import com.example.space_timetagger.sessions.domain.repository.SessionsRepository
import com.example.space_timetagger.sessions.presentation.models.SessionOverviewUiModel
import com.example.space_timetagger.sessions.presentation.sessionsList.SessionsListEvent
import com.example.space_timetagger.sessions.presentation.sessionsList.SessionsListViewState
import com.example.space_timetagger.sessions.presentation.sessionsList.SessionsViewModel
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
import org.mockito.internal.stubbing.answers.ReturnsElementsOf
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class SessionsListViewModelTest {
    private val lastChange = SessionsChange.Create(mockSessions.last().id)

    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @Mock
    private val mockSessionsRepository = mock<SessionsRepository>()

    private lateinit var viewModel: SessionsViewModel

    @Before
    fun setup() = runTest {
        whenever(mockSessionsRepository.sessionsAndLastChange()).thenReturn(
            flowOf(Pair(mockSessions, lastChange))
        )
        whenever(mockSessionsRepository.newSession()).thenAnswer(
            ReturnsElementsOf(List(10) { i -> "fake-id-of-new-session-$i" })
        )
        viewModel = SessionsViewModel(mockSessionsRepository)
    }

    @Test
    fun initialState_isSuccessState() = runTest {
        val initialViewState = viewModel.viewState.first()
        assertThat(initialViewState).hasClass<SessionsListViewState.Success>()
    }

    @Test
    fun initially_passesAlongRepositorySessions() = runTest {
        val initialViewState = viewModel.viewState.first()
        assertThat(initialViewState::sessions)
            .extracting(SessionOverviewUiModel::id)
            .isEqualTo(mockSessions.map(Session::id))
    }

    @Test
    fun initially_hasIdToNavigateToBasedOnRepository() = runTest {
        val initialViewState = viewModel.viewState.first()
        assertThat(initialViewState::idToNavigateTo).isEqualTo(lastChange.id)
    }

    // FIXME: test that if repository flow updates, view state will update

    @Test
    fun eventTapNewSessionButton_callsRepositoryFunc() = runTest {
        viewModel.handleEvent(SessionsListEvent.TapNewSessionButton)
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).newSession(null)
    }

    @Test
    fun eventTapConfirmDeleteSession_callsRepositoryFunc() = runTest {
        val fakeId = "fake-id"
        viewModel.handleEvent(SessionsListEvent.TapConfirmDeleteSession(fakeId))
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).deleteSession(fakeId)
    }

    @Test
    fun eventTapConfirmDeleteAllSessions_callsRepositoryFunc() = runTest {
        viewModel.handleEvent(SessionsListEvent.TapConfirmDeleteAllSessions)
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).deleteAllSessions()
    }

    @Test
    fun eventAutoNavigateToSession_callsRepositoryFunc() = runTest {
        assertThat(viewModel.viewState.first()::idToNavigateTo).isEqualTo(lastChange.id)

        viewModel.handleEvent(SessionsListEvent.AutoNavigateToSession(mockSessions.last().id))
        advanceUntilIdle()
        assertThat(viewModel.viewState.first()::idToNavigateTo).isNull()
    }
}
