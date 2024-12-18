package com.example.space_timetagger.sessions.presentation.sessionsList

import androidx.compose.runtime.Immutable
import com.example.space_timetagger.sessions.presentation.models.SessionOverviewUi

@Immutable
sealed interface SessionsListViewState {
    data object Loading : SessionsListViewState
    data class Success(
        val sessions: List<SessionOverviewUi>,
        val deleteAllIsEnabled: Boolean,
    ) : SessionsListViewState

    data class Refreshing(val sessions: List<SessionOverviewUi>) : SessionsListViewState
    data object Error : SessionsListViewState
}
