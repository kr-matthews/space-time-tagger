package com.example.space_timetagger.sessions.presentation.sessionsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.space_timetagger.App
import com.example.space_timetagger.sessions.domain.models.Session
import com.example.space_timetagger.sessions.domain.models.SessionsChange
import com.example.space_timetagger.sessions.domain.repository.SessionsRepository
import com.example.space_timetagger.sessions.presentation.models.toOverviewUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SessionsViewModel(
    private val sessionsRepository: SessionsRepository,
) : ViewModel() {
    private val sessions = sessionsRepository.sessions()

    private val lastChange: MutableStateFlow<SessionsChange?> = MutableStateFlow(null)

    private val lastIdNavigatedTo = MutableStateFlow<String?>(null)

    val viewState =
        combine(sessions, lastChange, lastIdNavigatedTo) { sessions, lastChange, idNavigatedTo ->
            val justCreatedId = (lastChange as? SessionsChange.Create)?.id
            val justCreatedIdExists = sessions.any { it.id == justCreatedId }
            val needsToNavigate = justCreatedIdExists && justCreatedId != idNavigatedTo
            val idToNavigateTo = if (needsToNavigate) justCreatedId else null

            SessionsListViewState.Success(
                sessions = sessions.map(Session::toOverviewUiModel),
                idToNavigateTo = idToNavigateTo,
                deleteAllIsEnabled = sessions.isNotEmpty(),
            )
        }

    fun handleEvent(event: SessionsListEvent) {
        when (event) {
            SessionsListEvent.TapSettings -> Unit // navigate, in compose
            SessionsListEvent.TapNewSessionButton -> createNewSession()
            is SessionsListEvent.TapSession -> Unit // navigate, in compose
            is SessionsListEvent.TapConfirmDeleteSession -> deleteSession(event.sessionId)
            SessionsListEvent.TapConfirmDeleteAllSessions -> deleteAllSessions()
            is SessionsListEvent.AutoNavigateToSession -> lastIdNavigatedTo.update { event.sessionId }
        }
    }

    private fun createNewSession(name: String? = null) {
        viewModelScope.launch {
            val id = sessionsRepository.newSession(name)
            lastChange.update { SessionsChange.Create(id) }
        }
    }

    private fun deleteSession(id: String) {
        viewModelScope.launch {
            sessionsRepository.deleteSession(id)
            lastChange.update { SessionsChange.Delete(id) }
        }
    }

    private fun deleteAllSessions() {
        viewModelScope.launch {
            sessionsRepository.deleteAllSessions()
            lastChange.update { SessionsChange.Clear }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class SessionsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SessionsViewModel(App.appModule.sessionsRepository) as T
    }
}