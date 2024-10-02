package com.example.space_timetagger.ui.sessions

import androidx.lifecycle.ViewModel
import com.example.space_timetagger.domain.model.SessionModel
import com.example.space_timetagger.domain.model.SessionsCallbacks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SessionsViewModel : ViewModel() {
    private val _sessions = MutableStateFlow<List<SessionModel>>(listOf())
    val sessions = _sessions.asStateFlow()

    private val _sessionIdToNavigateTo = MutableStateFlow<String?>(null)
    val sessionIdToNavigateTo = _sessionIdToNavigateTo.asStateFlow()

    fun clearSessionIdToNavigateTo() {
        _sessionIdToNavigateTo.update { null }
    }

    val callbacks = object : SessionsCallbacks {
        override fun new(name: String?) {
            val newSession = SessionModel(name)
            _sessions.update { it.toMutableList().apply { add(newSession) } }
            _sessionIdToNavigateTo.update { newSession.id }
        }

        override fun delete(id: String) {
            _sessions.update {
                it.toMutableList().apply { removeIf { session -> session.id == id } }
            }
        }

        override fun clearAll() {
            _sessions.update { listOf() }
        }
    }
}