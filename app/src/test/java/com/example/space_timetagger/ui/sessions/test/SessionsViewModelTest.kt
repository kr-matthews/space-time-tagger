package com.example.space_timetagger.ui.sessions.test

import com.example.space_timetagger.ui.sessions.SessionsViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SessionsViewModelTest {
    private val viewModel = SessionsViewModel()

    @Test
    fun initially_hasNoSessions() {
        val initialSessions = viewModel.sessions.value
        assert(initialSessions.isEmpty())
    }

    @Test
    fun initially_doesNotNavigateAway() {
        val initialSessionIdToNavigateTo = viewModel.sessionIdToNavigateTo.value
        assertNull(initialSessionIdToNavigateTo)
    }

    @Test
    fun newSessionCallback_createsNewSession() {
        val newSessionName = "New test Session"
        val initialSessions = viewModel.sessions.value
        viewModel.callbacks.new(newSessionName)
        val newSessions = viewModel.sessions.value
        assertEquals(initialSessions.size + 1, newSessions.size)
        assertEquals(newSessionName, newSessions.last().name)
    }

    @Test
    fun newSessionCallback_navigatesToNewSession() {
        viewModel.callbacks.new()
        val newSession = viewModel.sessions.value.last()
        val sessionIdToNavigateTo = viewModel.sessionIdToNavigateTo.value
        assertEquals(newSession.id, sessionIdToNavigateTo)
    }

    @Test
    fun clearSessionIdToNavigateTo_clearsSessionIdToNavigateTo() {
        viewModel.callbacks.new()
        viewModel.clearSessionIdToNavigateTo()
        val sessionIdToNavigateTo = viewModel.sessionIdToNavigateTo.value
        assertNull(sessionIdToNavigateTo)
    }

    @Test
    fun deleteSessionCallback_deletesExistingSession() {
        repeat(4) { viewModel.callbacks.new() }
        val preDeletionSessions = viewModel.sessions.value
        val deletedSession = preDeletionSessions[2]
        viewModel.callbacks.delete(deletedSession.id)
        val postDeletionSessions = viewModel.sessions.value
        assertEquals(preDeletionSessions.size - 1, postDeletionSessions.size)
        assert(postDeletionSessions.none { it.id == deletedSession.id })
    }

    @Test
    fun deleteSessionCallback_doesNothingWithUnusedId() {
        repeat(4) { viewModel.callbacks.new() }
        val preDeletionSessions = viewModel.sessions.value
        val fakeId = preDeletionSessions.foldRight("FakeSessionId_") { session, acc ->
            acc + session.id
        }
        viewModel.callbacks.delete(fakeId)
        val postDeletionSessions = viewModel.sessions.value
        assertEquals(preDeletionSessions, postDeletionSessions)
    }

    @Test
    fun clearAllSessionsCallback_deletesAllSessions() {
        repeat(4) { viewModel.callbacks.new() }
        viewModel.callbacks.clearAll()
        val postDeletionSessions = viewModel.sessions.value
        assert(postDeletionSessions.isEmpty())
    }
}
