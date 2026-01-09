package com.example.space_timetagger.sessions.presentation.sessionsList

import com.example.space_timetagger.sessions.presentation.models.SessionOverviewUiModel

val noSessions = listOf<SessionOverviewUiModel>()
val someSessions = listOf(
    SessionOverviewUiModel(name = "Session 1", progress = 0f),
    SessionOverviewUiModel(name = "Session 2", progress = 6 / 23f),
    SessionOverviewUiModel(name = null, progress = 33 / 34f),
    SessionOverviewUiModel(name = "Session 4 long name", progress = null),
    SessionOverviewUiModel(
        name = "Session 5 longest name, so long it doesn't fit in the space provided by the card and gets cut off",
        progress = 1f,
    ),
    SessionOverviewUiModel(name = "Session 6", progress = 0f),
    SessionOverviewUiModel(name = "Session 7", progress = 1f),
)
