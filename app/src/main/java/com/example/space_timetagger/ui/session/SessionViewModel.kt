package com.example.space_timetagger.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.space_timetagger.App
import com.example.space_timetagger.domain.model.SessionCallbacks
import com.example.space_timetagger.domain.model.TagModel
import com.example.space_timetagger.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.OffsetDateTime

class SessionViewModel(
    private val sessionsRepository: SessionsRepository,
) : ViewModel() {
    private val _name = MutableStateFlow<String?>(null)
    val name = _name.asStateFlow()

    private val _tags = MutableStateFlow(listOf<TagModel>())
    val tags = _tags.asStateFlow()

    val callbacks = object : SessionCallbacks {
        override fun setName(name: String?) {
            _name.update { name }
        }

        override fun addTag(now: OffsetDateTime) {
            val tag = TagModel(now)
            _tags.update { it.toMutableList().apply { add(tag) } }
        }

        override fun deleteTag(tag: TagModel) {
            _tags.update { it.toMutableList().apply { remove(tag) } }
        }

        override fun clearTags() {
            _tags.update { listOf() }
        }

    }
}

class SessionViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SessionViewModel(App.appModule.sessionsRepository) as T
    }
}