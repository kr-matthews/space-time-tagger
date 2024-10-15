package com.example.space_timetagger.data.repository

import com.example.space_timetagger.domain.model.SessionModel
import com.example.space_timetagger.domain.model.TagModel
import com.example.space_timetagger.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class SessionsRepositoryImpl : SessionsRepository {
    private val _sessions = MutableStateFlow(mutableListOf<SessionModel>())

    override fun sessions() = _sessions

    override fun session(id: String): Flow<SessionModel?> =
        _sessions.map { list -> list.find { session -> session.id == id } }

    override suspend fun newSession(name: String?): String {
        val newSession = SessionModel(name)
        _sessions.update { it.apply { add(newSession) } }
        return newSession.id
    }

    override suspend fun renameSession(id: String, newName: String?) {
        _sessions.update { list ->
            list.apply {
                val index = list.indexOfFirst { it.id == id }
                if (index > -1) {
                    list[index] = get(index).copy(name = newName)
                }
            }
        }
    }

    override suspend fun addTagToSession(id: String, tag: TagModel) {
        _sessions.update { list ->
            list.apply {
                val index = list.indexOfFirst { it.id == id }
                if (index > -1) {
                    val session = get(index)
                    val newTags = session.tags.toMutableList().apply { add(tag) }
                    list[index] = session.copy(tags = newTags)
                }
            }
        }
    }

    override suspend fun removeTagFromSession(id: String, tag: TagModel) {
        _sessions.update { list ->
            list.apply {
                val index = list.indexOfFirst { it.id == id }
                if (index > -1) {
                    val session = get(index)
                    val newTags = session.tags.toMutableList().apply { remove(tag) }
                    list[index] = session.copy(tags = newTags)
                }
            }
        }
    }

    override suspend fun removeAllTagsFromSession(id: String) {
        _sessions.update { list ->
            list.apply {
                val index = list.indexOfFirst { it.id == id }
                if (index > -1) {
                    list[index] = get(index).copy(tags = listOf())
                }
            }
        }
    }

    override suspend fun deleteSession(id: String) {
        _sessions.update { list -> list.apply { removeIf { it.id == id } } }
    }

    override suspend fun deleteAllSessions() {
        _sessions.update { mutableListOf() }
    }
}