package com.example.space_timetagger.sessions.presentation.sessionsList

import androidx.compose.runtime.Immutable
import com.example.space_timetagger.sessions.presentation.models.SessionOverviewUi

@Immutable
sealed class SessionsListState {
    data object Loading: SessionsListState()

    data class Success(val sessions: List<SessionOverviewUi>): SessionsListState()

    data class Refreshing(val sessions: List<SessionOverviewUi>): SessionsListState()

    data object Error: SessionsListState()
}
