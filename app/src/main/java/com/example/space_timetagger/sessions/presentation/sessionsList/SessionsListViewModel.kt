package com.example.space_timetagger.sessions.presentation.sessionsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.space_timetagger.App
import com.example.space_timetagger.sessions.domain.models.SessionsCallbacks
import com.example.space_timetagger.sessions.domain.repository.SessionsRepository
import com.example.space_timetagger.sessions.presentation.models.toOverviewUiModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SessionsViewModel(
    private val sessionsRepository: SessionsRepository,
) : ViewModel() {
    private val isLoading = MutableStateFlow(true)

    private val sessions = sessionsRepository.sessions().map { sessions ->
        sessions.map { session -> session.toOverviewUiModel() }
    }.onEach { isLoading.update { false } }

    val viewState = combine(sessions, isLoading) { sessions, isLoading ->
        when {
            isLoading -> SessionsListUiState.Refreshing(sessions)
            else -> SessionsListUiState.Success(sessions)
        }
    }

    private val _sessionIdToNavigateTo = Channel<String>()
    val sessionIdToNavigateTo = _sessionIdToNavigateTo.receiveAsFlow()

    val callbacks = object : SessionsCallbacks {
        override fun new(name: String?) {
            viewModelScope.launch {
                val newSessionId = sessionsRepository.newSession(name)
                _sessionIdToNavigateTo.send(newSessionId)
            }
        }

        override fun delete(id: String) {
            viewModelScope.launch {
                sessionsRepository.deleteSession(id)
            }
        }

        override fun deleteAll() {
            viewModelScope.launch {
                sessionsRepository.deleteAllSessions()
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class SessionsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SessionsViewModel(App.appModule.sessionsRepository) as T
    }
}