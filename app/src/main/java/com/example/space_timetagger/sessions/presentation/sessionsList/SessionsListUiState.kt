package com.example.space_timetagger.sessions.presentation.sessionsList

import androidx.compose.runtime.Immutable
import com.example.space_timetagger.sessions.presentation.models.SessionOverviewUi

@Immutable
sealed class SessionsListUiState {
    data object Loading : SessionsListUiState()

    data class Success(val sessions: List<SessionOverviewUi>) : SessionsListUiState()

    data class Refreshing(val sessions: List<SessionOverviewUi>) : SessionsListUiState()

    data object Error : SessionsListUiState()
}
