package com.example.space_timetagger.sessions.presentation.sessionsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.space_timetagger.App
import com.example.space_timetagger.sessions.domain.models.Session
import com.example.space_timetagger.sessions.domain.repository.SessionsRepository
import com.example.space_timetagger.sessions.presentation.models.toOverviewUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SessionsViewModel(
    private val sessionsRepository: SessionsRepository,
) : ViewModel() {
    private val sessions = sessionsRepository.sessions().map { sessions ->
        sessions.map(Session::toOverviewUiModel)
    }

    val viewState = sessions.map { sessions ->
        SessionsListViewState.Success(
            sessions = sessions,
            deleteAllIsEnabled = sessions.isNotEmpty(),
        )
    }

    private val _sessionIdToNavigateTo = MutableStateFlow<String?>(null)
    val sessionIdToNavigateTo = _sessionIdToNavigateTo.asStateFlow()

    fun clearSessionIdToNavigateTo() = _sessionIdToNavigateTo.update { null }

    fun handleEvent(event: SessionsListEvent) {
        when (event) {
            SessionsListEvent.TapSettings -> Unit // navigate, in compose
            SessionsListEvent.TapNewSessionButton -> createNewSessionAndNavigateToIt()
            is SessionsListEvent.TapSession -> navigateToSession(event.sessionId)
            is SessionsListEvent.TapConfirmDeleteSession -> deleteSession(event.sessionId)
            SessionsListEvent.TapConfirmDeleteAllSessions -> deleteAllSessions()
        }
    }

    private fun createNewSessionAndNavigateToIt(name: String? = null) {
        viewModelScope.launch {
            val newSessionId = sessionsRepository.newSession(name)
            navigateToSession(newSessionId)
        }
    }

    private fun navigateToSession(id: String) {
        viewModelScope.launch {
            _sessionIdToNavigateTo.update { id }
        }
    }

    private fun deleteSession(id: String) {
        viewModelScope.launch {
            sessionsRepository.deleteSession(id)
        }
    }

    private fun deleteAllSessions() {
        viewModelScope.launch {
            sessionsRepository.deleteAllSessions()
        }
    }
}

@Suppress("UNCHECKED_CAST")
class SessionsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SessionsViewModel(App.appModule.sessionsRepository) as T
    }
}