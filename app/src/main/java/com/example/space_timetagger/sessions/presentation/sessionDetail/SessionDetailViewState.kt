package com.example.space_timetagger.sessions.presentation.sessionDetail

import androidx.compose.runtime.Immutable
import com.example.space_timetagger.sessions.presentation.models.SessionDetailUiModel

@Immutable
sealed interface SessionDetailViewState {
    data object Loading : SessionDetailViewState
    data class Success(val session: SessionDetailUiModel) : SessionDetailViewState
    data class Refreshing(val session: SessionDetailUiModel) : SessionDetailViewState
    data object Error : SessionDetailViewState
}
