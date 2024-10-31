package com.example.space_timetagger.ui.sessionsList.test

import com.example.space_timetagger.domain.repository.SessionsRepository
import com.example.space_timetagger.ui.CoroutineTestRule
import com.example.space_timetagger.ui.mockSessions
import com.example.space_timetagger.ui.sessionsList.SessionsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @Mock
    private val mockSessionsRepository = mock<SessionsRepository>()

    private lateinit var viewModel: SessionsViewModel

    @Before
    fun setup() = runTest {
        whenever(mockSessionsRepository.sessions()).thenReturn(flowOf(mockSessions))
        whenever(mockSessionsRepository.newSession()).thenAnswer(
            ReturnsElementsOf(List(10) { i -> "fake-id-of-new-session-$i" })
        )
        viewModel = SessionsViewModel(mockSessionsRepository)
    }

    @Test
    fun initially_passesAlongRepositorySessions() = runTest {
        val initialSessions = viewModel.sessions.first()
        assertEquals(mockSessions.map { it.id }, initialSessions.map { it.id })
    }

    @Test
    fun initially_doesNotNavigateAway() {
        val initialSessionIdToNavigateTo = viewModel.sessionIdToNavigateTo.value
        assertNull(initialSessionIdToNavigateTo)
    }

    @Test
    fun newSessionCallback_callsRepositoryFunc() = runTest {
        val newSessionName = "New test Session"
//        val initialSessions = viewModel.sessions.first()
        viewModel.callbacks.new(newSessionName)
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).newSession(newSessionName)
//        val newSessions = viewModel.sessions.first()
//        assertEquals(initialSessions.size + 1, newSessions.size)
//        assertEquals(newSessionName, newSessions.last().name)
    }

    @Test
    fun newSessionCallback_setsIdToNavigateTo() = runTest {
        viewModel.callbacks.new()
        advanceUntilIdle()
        val sessionIdToNavigateTo = viewModel.sessionIdToNavigateTo.value
        assertNotNull(sessionIdToNavigateTo)
    }

    @Test
    fun clearSessionIdToNavigateTo_clearsSessionIdToNavigateTo() = runTest {
        viewModel.callbacks.new()
        advanceUntilIdle()
        viewModel.clearSessionIdToNavigateTo()
        val sessionIdToNavigateTo = viewModel.sessionIdToNavigateTo.value
        assertNull(sessionIdToNavigateTo)
    }

    @Test
    fun deleteSessionCallback_callsRepositoryFunc() = runTest {
        val fakeId = "fake-id"
        viewModel.callbacks.delete(fakeId)
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).deleteSession(fakeId)
    }

    @Test
    fun deleteAllSessionsCallback_callsRepositoryFunc() = runTest {
        viewModel.callbacks.deleteAll()
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).deleteAllSessions()
    }
}
