package com.example.space_timetagger.data.repository

import com.example.space_timetagger.domain.models.Session
import com.example.space_timetagger.domain.models.Tag
import com.example.space_timetagger.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class SessionsRepositoryImpl : SessionsRepository {
    private val _sessions = MutableStateFlow(listOf<Session>())

    override fun sessions() = _sessions.asStateFlow()

    override fun session(id: String): Flow<Session?> =
        _sessions.map { list -> list.find { session -> session.id == id } }

    override suspend fun newSession(name: String?): String {
        val newSession = Session(name = name)
        _sessions.update { it.toMutableList().apply { add(newSession) } }
        return newSession.id
    }

    override suspend fun renameSession(id: String, newName: String?) {
        _sessions.update { list ->
            list.toMutableList().apply {
                val index = list.indexOfFirst { it.id == id }
                if (index > -1) {
                    set(index, get(index).copy(name = newName))
                }
            }.toList()
        }
    }

    override suspend fun addTagToSession(id: String, tag: Tag) {
        _sessions.update { list ->
            list.toMutableList().apply {
                val index = list.indexOfFirst { it.id == id }
                if (index > -1) {
                    val session = get(index)
                    val newTags = session.tags.toMutableList().apply { add(tag) }
                    set(index, session.copy(tags = newTags))
                }
            }.toList()
        }
    }

    override suspend fun removeTagFromSession(id: String, tagId: String) {
        _sessions.update { list ->
            list.toMutableList().apply {
                val index = list.indexOfFirst { it.id == id }
                if (index > -1) {
                    val session = get(index)
                    val newTags = session.tags.toMutableList().filterNot { it.id == tagId }
                    set(index, session.copy(tags = newTags))
                }
            }.toList()
        }
    }

    override suspend fun removeAllTagsFromSession(id: String) {
        _sessions.update { list ->
            list.toMutableList().apply {
                val index = list.indexOfFirst { it.id == id }
                if (index > -1) {
                    set(index, get(index).copy(tags = listOf()))
                }
            }.toList()
        }
    }

    override suspend fun deleteSession(id: String) {
        _sessions.update { list ->
            list.toMutableList().apply { removeIf { it.id == id } }.toList()
        }
    }

    override suspend fun deleteAllSessions() {
        _sessions.update { listOf() }
    }
}