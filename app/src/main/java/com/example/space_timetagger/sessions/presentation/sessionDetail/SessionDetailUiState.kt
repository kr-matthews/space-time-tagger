package com.example.space_timetagger.sessions.presentation.sessionDetail

import androidx.compose.runtime.Immutable
import com.example.space_timetagger.sessions.presentation.models.SessionDetailUi

@Immutable
sealed class SessionDetailUiState {
    data object Loading : SessionDetailUiState()

    data class Success(val session: SessionDetailUi) : SessionDetailUiState()

    data class Refreshing(val session: SessionDetailUi) : SessionDetailUiState()

    data object Error : SessionDetailUiState()
}
