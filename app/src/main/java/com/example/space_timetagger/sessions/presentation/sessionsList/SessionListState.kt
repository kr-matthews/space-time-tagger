package com.example.space_timetagger.sessions.presentation.sessionsList

import androidx.compose.runtime.Immutable
import com.example.space_timetagger.sessions.presentation.models.SessionOverviewUi

@Immutable
sealed class SessionListState {
    data object Loading: SessionListState()

    data class Success(val sessions: List<SessionOverviewUi>): SessionListState()

    data class Refreshing(val sessions: List<SessionOverviewUi>): SessionListState()

    data object Error: SessionListState()
}
