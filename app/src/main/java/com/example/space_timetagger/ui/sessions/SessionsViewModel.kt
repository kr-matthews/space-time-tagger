package com.example.space_timetagger.ui.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.space_timetagger.App
import com.example.space_timetagger.domain.model.SessionsCallbacks
import com.example.space_timetagger.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SessionsViewModel(
    private val sessionsRepository: SessionsRepository,
) : ViewModel() {
    val sessions = sessionsRepository.sessions()

    private val _sessionIdToNavigateTo = MutableStateFlow<String?>(null)
    val sessionIdToNavigateTo = _sessionIdToNavigateTo.asStateFlow()

    fun clearSessionIdToNavigateTo() {
        _sessionIdToNavigateTo.update { null }
    }

    val callbacks = object : SessionsCallbacks {
        override fun new(name: String?) {
            viewModelScope.launch {
                val newSessionId = sessionsRepository.newSession(name)
                _sessionIdToNavigateTo.update { newSessionId }
            }
        }

        override fun delete(id: String) {
            viewModelScope.launch {
                sessionsRepository.deleteSession(id)
            }
        }

        override fun clearAll() {
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