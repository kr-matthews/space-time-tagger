package com.example.space_timetagger.sessions.data.repository

import com.example.space_timetagger.sessions.domain.datasource.SessionsDataSource
import com.example.space_timetagger.sessions.domain.models.Session
import com.example.space_timetagger.sessions.domain.models.SessionChange
import com.example.space_timetagger.sessions.domain.models.SessionsChange
import com.example.space_timetagger.sessions.domain.models.Tag
import com.example.space_timetagger.sessions.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class SessionsRepositoryImpl(
    private val sessionsDataSource: SessionsDataSource,
) : SessionsRepository {

    override fun sessions() = _sessionsWithChangesWithChange.asStateFlow().map { (list, _) ->
        list.map { (session, _) -> session }
    }
    private val _sessionsWithChangesWithChange: MutableStateFlow<Pair<List<Pair<Session, SessionChange>>, SessionsChange?>> =
        MutableStateFlow(Pair(emptyList(), null))


    override fun sessionsAndLastChange(): Flow<Pair<List<Session>, SessionsChange?>> =
        _sessionsWithChangesWithChange.asStateFlow().map { (list, lastChange) ->
            Pair(list.map { (session, _) -> session }, lastChange)
        }

    override fun session(id: String): Flow<Session?> =
        _sessionsWithChangesWithChange.map { (list, _) -> list.find { (session, _) -> session.id == id }?.first }

    override fun sessionAndLastChange(id: String): Flow<Pair<Session, SessionChange>?> =
        _sessionsWithChangesWithChange.map { (list, _) -> list.find { (session, _) -> session.id == id } }

    override suspend fun newSession(name: String?): String {
        val newSession = Session(name = name)
        _sessionsWithChangesWithChange.update { (list, _) ->
            Pair(
                list.toMutableList().apply { add(Pair(newSession, SessionChange.Create)) },
                SessionsChange.Create(newSession.id),
            )
        }
        // TODO: remove return value
        return newSession.id
    }

    override suspend fun renameSession(id: String, newName: String?) {
        _sessionsWithChangesWithChange.update { (list, _) ->
            Pair(
                list.toMutableList().apply {
                    val index = list.indexOfFirst { it.first.id == id }
                    if (index > -1) {
                        val (session, _) = get(index)
                        val renamedSession = session.copy(name = newName)
                        set(index, Pair(renamedSession, SessionChange.Rename))
                    }
                }.toList(),
                SessionsChange.Edit(id),
            )
        }
    }

    override suspend fun addTagToSession(id: String, tag: Tag) {
        _sessionsWithChangesWithChange.update { (list, _) ->
            Pair(
                list.toMutableList().apply {
                    val index = list.indexOfFirst { it.first.id == id }
                    if (index > -1) {
                        val (session, _) = get(index)
                        val newTags = session.tags.toMutableList().apply { add(tag) }
                        val updatedSession = session.copy(tags = newTags)
                        set(index, Pair(updatedSession, SessionChange.AddTag(tag.id)))
                    }
                }.toList(),
                SessionsChange.Edit(id),
            )
        }
    }

    override suspend fun removeTagFromSession(id: String, tagId: String) {
        _sessionsWithChangesWithChange.update { (list, _) ->
            Pair(
                list.toMutableList().apply {
                    val index = list.indexOfFirst { it.first.id == id }
                    if (index > -1) {
                        val (session, _) = get(index)
                        val newTags = session.tags.toMutableList().filterNot { it.id == tagId }
                        val updatedSession = session.copy(tags = newTags)
                        set(index, Pair(updatedSession, SessionChange.DeleteTag(tagId)))
                    }
                }.toList(),
                SessionsChange.Edit(id),
            )
        }
    }

    override suspend fun removeAllTagsFromSession(id: String) {
        _sessionsWithChangesWithChange.update { (list, _) ->
            Pair(
                list.toMutableList().apply {
                    val index = list.indexOfFirst { it.first.id == id }
                    if (index > -1) {
                        val (session, _) = get(index)
                        val updatedSession = session.copy(tags = emptyList())
                        set(index, Pair(updatedSession, SessionChange.Clear))
                    }
                }.toList(),
                SessionsChange.Edit(id),
            )
        }
    }

    override suspend fun deleteSession(id: String) {
        _sessionsWithChangesWithChange.update { (list, _) ->
            Pair(
                list.toMutableList().apply { removeIf { it.first.id == id } }.toList(),
                SessionsChange.Delete(id),
            )
        }
    }

    override suspend fun deleteAllSessions() {
        _sessionsWithChangesWithChange.update { Pair(listOf(), SessionsChange.Clear) }
    }
}