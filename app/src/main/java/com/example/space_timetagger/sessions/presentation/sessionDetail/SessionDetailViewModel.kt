package com.example.space_timetagger.sessions.presentation.sessionDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.space_timetagger.App
import com.example.space_timetagger.core.domain.repository.PreferencesRepository
import com.example.space_timetagger.sessions.domain.models.Session
import com.example.space_timetagger.sessions.domain.models.Tag
import com.example.space_timetagger.sessions.domain.repository.SessionsRepository
import com.example.space_timetagger.sessions.presentation.models.SessionDetailUiModel
import com.example.space_timetagger.sessions.presentation.models.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

class SessionViewModel(
    private val sessionId: String,
    private val sessionsRepository: SessionsRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    private val session = sessionsRepository.session(sessionId)

    private val nameIsBeingEdited = MutableStateFlow(false)

    val viewState =
        combine(session, nameIsBeingEdited) { session, nameIsBeingEdited ->
            when {
                session == null -> SessionDetailViewState.Error

//                isLoading -> SessionDetailViewState.Refreshing(
//                    buildSessionDetailUiModel(session, nameIsBeingEdited)
//                )

                else -> SessionDetailViewState.Success(
                    buildSessionDetailUiModel(session, nameIsBeingEdited)
                )
            }
        }

    fun handleEvent(event: SessionDetailEvent) {
        when (event) {
            SessionDetailEvent.TapBack -> Unit // navigate, in compose
            SessionDetailEvent.TapSettings -> Unit // navigate, in compose
            SessionDetailEvent.TapName -> nameIsBeingEdited.update { true }
            is SessionDetailEvent.ChangeName -> setName(event.newName)
            SessionDetailEvent.TapNameDoneEditing -> nameIsBeingEdited.update { false }
            is SessionDetailEvent.TapNewTagButton -> addTag(event.time)
            is SessionDetailEvent.TapConfirmDeleteTag -> deleteTag(event.tagId)
            SessionDetailEvent.TapConfirmDeleteAllTags -> deleteAllTags()
        }
    }

    private fun setName(name: String?) {
        viewModelScope.launch {
            sessionsRepository.renameSession(sessionId, name)
        }
    }

    private fun addTag(now: OffsetDateTime) {
        val tag = Tag(dateTime = now)
        viewModelScope.launch {
            sessionsRepository.addTagToSession(sessionId, tag)
        }
    }

    private fun deleteTag(id: String) {
        viewModelScope.launch {
            sessionsRepository.removeTagFromSession(sessionId, id)
        }
    }

    private fun deleteAllTags() {
        viewModelScope.launch {
            sessionsRepository.removeAllTagsFromSession(sessionId)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class SessionViewModelFactory(
    private val sessionId: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SessionViewModel(
            sessionId = sessionId,
            sessionsRepository = App.appModule.sessionsRepository,
            preferencesRepository = App.appModule.preferencesRepository,
        ) as T
    }
}

private fun buildSessionDetailUiModel(session: Session, nameIsBeingEdited: Boolean) =
    SessionDetailUiModel(
        id = session.id,
        name = session.name,
        nameIsBeingEdited = nameIsBeingEdited,
        tags = session.tags.map(Tag::toUiModel),
        deleteAllIsEnabled = session.tags.isNotEmpty(),
    )
