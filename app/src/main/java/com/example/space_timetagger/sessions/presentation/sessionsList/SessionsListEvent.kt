package com.example.space_timetagger.sessions.presentation.sessionsList

sealed interface SessionsListEvent {
    data object TapSettings : SessionsListEvent
    data class TapSession(val sessionId: String) : SessionsListEvent
    data object TapNewSessionButton : SessionsListEvent
    data class TapConfirmDeleteSession(val sessionId: String) : SessionsListEvent
    data object TapConfirmDeleteAllSessions : SessionsListEvent
    data class AutoNavigateToSession(val sessionId: String) : SessionsListEvent
}