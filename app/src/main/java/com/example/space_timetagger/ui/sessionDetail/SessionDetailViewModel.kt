package com.example.space_timetagger.ui.sessionDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.space_timetagger.App
import com.example.space_timetagger.domain.model.SessionCallbacks
import com.example.space_timetagger.domain.model.TagModel
import com.example.space_timetagger.domain.repository.SessionsRepository
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

class SessionViewModel(
    sessionId: String,
    private val sessionsRepository: SessionsRepository,
) : ViewModel() {
    val session = sessionsRepository.session(sessionId)

    val callbacks = object : SessionCallbacks {
        override fun setName(name: String?) {
            viewModelScope.launch {
                sessionsRepository.renameSession(sessionId, name)
            }
        }

        override fun addTag(now: OffsetDateTime) {
            val tag = TagModel(now)
            viewModelScope.launch {
                sessionsRepository.addTagToSession(sessionId, tag)
            }
        }

        override fun deleteTag(tag: TagModel) {
            viewModelScope.launch {
                sessionsRepository.removeTagFromSession(sessionId, tag)
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