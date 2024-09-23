package com.example.space_timetagger.ui.sessions

import androidx.lifecycle.ViewModel
import com.example.space_timetagger.domain.model.SessionModel
import com.example.space_timetagger.domain.model.SessionsCallbacks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class SessionsViewModel(
    initialList: List<SessionModel> = listOf(),
) : ViewModel() {
    private val _sessions = MutableStateFlow(initialList)
    val sessions = _sessions.asStateFlow()

    private val _selectedSessionId = MutableStateFlow<UUID?>(null)
    val selectedSessionId = _selectedSessionId.asStateFlow()

    val callbacks = object : SessionsCallbacks {
        override fun new(name: String?) {
            _sessions.update {
                it.toMutableList().apply { add(SessionModel(name ?: "Untitled")) }
            }
        }

        override fun delete(id: UUID) {
            if (selectedSessionId.value == id) {
                _selectedSessionId.update { null }
            }
            _sessions.update {
                it.toMutableList().apply { removeIf { session -> session.id == id } }
            }
        }

        override fun clearAll() {
            _selectedSessionId.update { null }
            _sessions.update { listOf() }
        }
    }
}