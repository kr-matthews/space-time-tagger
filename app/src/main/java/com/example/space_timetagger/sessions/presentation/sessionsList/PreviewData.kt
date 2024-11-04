package com.example.space_timetagger.sessions.presentation.sessionsList

import com.example.space_timetagger.sessions.domain.models.SessionsCallbacks
import com.example.space_timetagger.sessions.presentation.models.SessionOverviewUi

@Suppress("EmptyFunctionBlock")
val dummySessionsCallbacks = object : SessionsCallbacks {
    override fun new(name: String?) {}
    override fun delete(id: String) {}
    override fun deleteAll() {}
}

val noSessions = listOf<SessionOverviewUi>()
val someSessions = listOf(
    SessionOverviewUi(name = "Session 1"),
    SessionOverviewUi(name = "Session 2"),
    SessionOverviewUi(name = "Session 3"),
    SessionOverviewUi(name = "Session 4 long name"),
    SessionOverviewUi(name = "Session 5 longest name, so long it doesn't fit in the space"),
    SessionOverviewUi(name = "Session 6"),
    SessionOverviewUi(name = "Session 7"),
)
