package com.example.space_timetagger.sessions.presentation.sessionDetail

import androidx.compose.runtime.Immutable
import com.example.space_timetagger.sessions.presentation.models.SessionDetailUi

@Immutable
sealed class SessionDetailState {
    data object Loading: SessionDetailState()

    data class Success(val session: SessionDetailUi): SessionDetailState()

    data class Refreshing(val session: SessionDetailUi): SessionDetailState()

    data object Error: SessionDetailState()
}
