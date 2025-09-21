package com.example.space_timetagger.sessions.presentation.sessionDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.space_timetagger.App
import com.example.space_timetagger.core.domain.repository.PreferencesRepository
import com.example.space_timetagger.location.domain.repository.LocationRepository
import com.example.space_timetagger.sessions.domain.models.Session
import com.example.space_timetagger.sessions.domain.models.SessionChange
import com.example.space_timetagger.sessions.domain.models.Tag
import com.example.space_timetagger.sessions.domain.repository.SessionsRepository
import com.example.space_timetagger.sessions.presentation.models.SessionDetailUiModel
import com.example.space_timetagger.sessions.presentation.models.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class SessionViewModel(
    private val sessionId: String,
    private val sessionsRepository: SessionsRepository,
    private val preferencesRepository: PreferencesRepository,
    private val locationRepository: LocationRepository,
) : ViewModel() {
    private val sessionAndLastChange = sessionsRepository.sessionAndLastChange(sessionId)

    private val tapAnywhereIsEnabled = preferencesRepository.tapAnywhereIsEnabled

    private val nameIsBeingEdited = MutableStateFlow(false)

    private val scrolledToTagId = MutableStateFlow<String?>(null)

    val viewState =
        combine(
            sessionAndLastChange,
            tapAnywhereIsEnabled,
            nameIsBeingEdited,
            scrolledToTagId
        ) { sessionAndLastChange, tapAnywhereIsEnabled, nameIsBeingEdited, scrolledToTagId ->
            when {
                sessionAndLastChange == null -> SessionDetailViewState.Error
                else -> SessionDetailViewState.Success(
                    buildSessionDetailUiModel(
                        sessionAndLastChange,
                        scrolledToTagId,
                        tapAnywhereIsEnabled,
                        nameIsBeingEdited,
                    )
                )
            }
        }

    fun handleEvent(event: SessionDetailEvent) {
        when (event) {
            SessionDetailEvent.TapBack -> Unit // navigate, in compose
            SessionDetailEvent.TapSettings -> Unit // navigate, in compose
            SessionDetailEvent.TapEdit -> nameIsBeingEdited.update { true }
            is SessionDetailEvent.ConfirmNameEdit -> onDoneEditingName(event.newName)
            SessionDetailEvent.CancelNameEdit -> nameIsBeingEdited.update { false }
            is SessionDetailEvent.TapNewTagButton -> addTag(event.time)
            is SessionDetailEvent.TapConfirmDeleteTag -> deleteTag(event.tagId)
            SessionDetailEvent.TapConfirmDeleteAllTags -> deleteAllTags()
            is SessionDetailEvent.TapAnywhere -> onTapAnywhere(event.time)
            is SessionDetailEvent.AutoScrollToTag -> scrolledToTagId.update { event.id }
        }
    }

    private fun onDoneEditingName(newName: String?) {
        setName(newName?.trim()?.ifBlank { null })
        nameIsBeingEdited.update { false }
    }

    private fun setName(name: String?) {
        viewModelScope.launch {
            sessionsRepository.renameSession(sessionId, name)
        }
    }

    private fun addTag(now: LocalDateTime) {
        viewModelScope.launch {
            val taggingLocationIsEnabled =
                preferencesRepository.taggingLocationIsEnabled.firstOrNull() == true
            val currentLocation = if (taggingLocationIsEnabled) {
                locationRepository.findCurrentLocation()
            } else {
                null
            }
            val tag = Tag(dateTime = now, location = currentLocation)
            sessionsRepository.addTagToSession(sessionId, tag)
        }
    }

    private fun deleteTag(id: String) {
        viewModelScope.launch {
            sessionsRepository.removeTag(id)
        }
    }

    private fun deleteAllTags() {
        viewModelScope.launch {
            sessionsRepository.removeAllTagsFromSession(sessionId)
        }
    }

    private fun onTapAnywhere(time: LocalDateTime) {
        viewModelScope.launch {
            if (tapAnywhereIsEnabled.firstOrNull() == true) {
                addTag(time)
            }
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
            locationRepository = App.appModule.locationRepository,
        ) as T
    }
}

private fun buildSessionDetailUiModel(
    sessionAndLastChange: Pair<Session, SessionChange>,
    scrolledToTagId: String?,
    tapAnywhereIsEnabled: Boolean,
    nameIsBeingEdited: Boolean,
): SessionDetailUiModel {
    val (session, lastChange) = sessionAndLastChange
    val justAddedTagId = (lastChange as? SessionChange.AddTag)?.id
    val needToScrollToJustAddedTag = justAddedTagId != null && justAddedTagId != scrolledToTagId
    val tagIdToScrollTo = if (needToScrollToJustAddedTag) justAddedTagId else null

    return SessionDetailUiModel(
        id = session.id,
        name = session.name,
        nameIsBeingEdited = nameIsBeingEdited,
        tags = session.tags.map(Tag::toUiModel),
        tagIdToScrollTo = tagIdToScrollTo,
        deleteAllIsEnabled = session.tags.isNotEmpty(),
        tapAnywhereIsEnabled = tapAnywhereIsEnabled,
    )
}
