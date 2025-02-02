package com.example.space_timetagger.sessions.presentation.sessionsList

import androidx.compose.runtime.Immutable
import com.example.space_timetagger.sessions.presentation.models.SessionOverviewUiModel

@Immutable
sealed interface SessionsListViewState {
    data object Loading : SessionsListViewState
    data class Success(
        val sessions: List<SessionOverviewUiModel>,
        val idToNavigateTo: String?,
        val deleteAllIsEnabled: Boolean,
    ) : SessionsListViewState

    data class Refreshing(val sessions: List<SessionOverviewUiModel>) : SessionsListViewState
    data object Error : SessionsListViewState
}
