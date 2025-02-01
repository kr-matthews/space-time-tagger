package com.example.space_timetagger.sessions.data.repository

import com.example.space_timetagger.sessions.domain.models.Session
import com.example.space_timetagger.sessions.domain.models.SessionChange
import com.example.space_timetagger.sessions.domain.models.Tag
import com.example.space_timetagger.sessions.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class SessionsRepositoryImpl : SessionsRepository {
    private val _sessionsWithChanges = MutableStateFlow(listOf<Pair<Session, SessionChange>>())

    override fun sessions() = _sessionsWithChanges.asStateFlow().map { sessions ->
        sessions.map { (session, _) -> session }
    }

    override fun session(id: String): Flow<Session?> =
        _sessionsWithChanges.map { list -> list.find { (session, _) -> session.id == id }?.first }

    override fun sessionAndLastChange(id: String): Flow<Pair<Session, SessionChange>?> =
        _sessionsWithChanges.map { list -> list.find { (session, _) -> session.id == id } }

    override suspend fun newSession(name: String?): String {
        val newSession = Session(name = name)
        _sessionsWithChanges.update {
            it.toMutableList().apply { add(Pair(newSession, SessionChange.Create)) }
        }
        // TODO: remove return value
        return newSession.id
    }

    override suspend fun renameSession(id: String, newName: String?) {
        _sessionsWithChanges.update { list ->
            list.toMutableList().apply {
                val index = list.indexOfFirst { it.first.id == id }
                if (index > -1) {
                    val (session, _) = get(index)
                    val renamedSession = session.copy(name = newName)
                    set(index, Pair(renamedSession, SessionChange.Rename))
                }
            }.toList()
        }
    }

    override suspend fun addTagToSession(id: String, tag: Tag) {
        _sessionsWithChanges.update { list ->
            list.toMutableList().apply {
                val index = list.indexOfFirst { it.first.id == id }
                if (index > -1) {
                    val (session, _) = get(index)
                    val newTags = session.tags.toMutableList().apply { add(tag) }
                    val updatedSession = session.copy(tags = newTags)
                    set(index, Pair(updatedSession, SessionChange.AddTag(tag.id)))
                }
            }.toList()
        }
    }

    override suspend fun removeTagFromSession(id: String, tagId: String) {
        _sessionsWithChanges.update { list ->
            list.toMutableList().apply {
                val index = list.indexOfFirst { it.first.id == id }
                if (index > -1) {
                    val (session, _) = get(index)
                    val newTags = session.tags.toMutableList().filterNot { it.id == tagId }
                    val updatedSession = session.copy(tags = newTags)
                    set(index, Pair(updatedSession, SessionChange.DeleteTag(tagId)))
                }
            }.toList()
        }
    }

    override suspend fun removeAllTagsFromSession(id: String) {
        _sessionsWithChanges.update { list ->
            list.toMutableList().apply {
                val index = list.indexOfFirst { it.first.id == id }
                if (index > -1) {
                    val (session, _) = get(index)
                    val updatedSession = session.copy(tags = emptyList())
                    set(index, Pair(updatedSession, SessionChange.Clear))
                }
            }.toList()
        }
    }

    override suspend fun deleteSession(id: String) {
        _sessionsWithChanges.update { list ->
            list.toMutableList().apply { removeIf { it.first.id == id } }.toList()
        }
    }

    override suspend fun deleteAllSessions() {
        _sessionsWithChanges.update { listOf() }
    }
}