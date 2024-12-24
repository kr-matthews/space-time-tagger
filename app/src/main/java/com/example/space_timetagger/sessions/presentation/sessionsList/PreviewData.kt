package com.example.space_timetagger.sessions.presentation.sessionsList

import com.example.space_timetagger.sessions.presentation.models.SessionOverviewUiModel

val noSessions = listOf<SessionOverviewUiModel>()
val someSessions = listOf(
    SessionOverviewUiModel(name = "Session 1"),
    SessionOverviewUiModel(name = "Session 2"),
    SessionOverviewUiModel(name = null),
    SessionOverviewUiModel(name = "Session 4 long name"),
    SessionOverviewUiModel(name = "Session 5 longest name, so long it doesn't fit in the space"),
    SessionOverviewUiModel(name = "Session 6"),
    SessionOverviewUiModel(name = "Session 7"),
)
