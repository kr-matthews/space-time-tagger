package com.example.space_timetagger.sessions.presentation.sessionDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.space_timetagger.App
import com.example.space_timetagger.sessions.domain.models.SessionCallbacks
import com.example.space_timetagger.sessions.domain.models.Tag
import com.example.space_timetagger.sessions.domain.repository.SessionsRepository
import com.example.space_timetagger.sessions.presentation.models.toDetailUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

class SessionViewModel(
    sessionId: String,
    private val sessionsRepository: SessionsRepository,
) : ViewModel() {
    private val isLoading = MutableStateFlow(true)

    private val session = sessionsRepository.session(sessionId).map { it?.toDetailUiModel() }
        .onEach { isLoading.update { false } }

    val viewState = combine(session, isLoading) { session, isLoading ->
        when {
            session == null -> SessionDetailState.Error
            isLoading -> SessionDetailState.Refreshing(session)
            else -> SessionDetailState.Success(session)
        }
    }

    val callbacks = object : SessionCallbacks {
        override fun setName(name: String?) {
            viewModelScope.launch {
                sessionsRepository.renameSession(sessionId, name)
            }
        }

        override fun addTag(now: OffsetDateTime) {
            val tag = Tag(dateTime = now)
            viewModelScope.launch {
                sessionsRepository.addTagToSession(sessionId, tag)
            }
        }

        override fun deleteTag(id: String) {
            viewModelScope.launch {
                sessionsRepository.removeTagFromSession(sessionId, id)
            }
        }

        override fun deleteAllTags() {
            viewModelScope.launch {
                sessionsRepository.removeAllTagsFromSession(sessionId)
            }
        }

    }
}

@Suppress("UNCHECKED_CAST")
class SessionViewModelFactory(private val sessionId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SessionViewModel(sessionId, App.appModule.sessionsRepository) as T
    }
}