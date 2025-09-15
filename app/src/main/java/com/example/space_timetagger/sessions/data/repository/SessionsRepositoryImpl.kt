package com.example.space_timetagger.sessions.data.repository

import com.example.space_timetagger.sessions.domain.datasource.SessionsDataSource
import com.example.space_timetagger.sessions.domain.models.Session
import com.example.space_timetagger.sessions.domain.models.SessionChange
import com.example.space_timetagger.sessions.domain.models.SessionsChange
import com.example.space_timetagger.sessions.domain.models.Tag
import com.example.space_timetagger.sessions.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SessionsRepositoryImpl(
    private val sessionsDataSource: SessionsDataSource,
) : SessionsRepository {

    override fun sessions() = sessionsDataSource.getSessions()

    // TODO: bring back last SessionChange
    override fun sessionsAndLastChange(): Flow<Pair<List<Session>, SessionsChange?>> =
        sessions().map { Pair(it, null) }

    override fun session(id: String): Flow<Session?> = sessionsDataSource.getSession(id)

    // TODO: fix last SessionChange
    override fun sessionAndLastChange(id: String): Flow<Pair<Session, SessionChange>?> =
        session(id).map { it?.let { Pair(it, SessionChange.Clear) } }

    override suspend fun newSession(name: String?): String {
        val newSession = Session(name = name)
        sessionsDataSource.upsertSession(newSession)
        // TODO: remove return value (?)
        return newSession.id
    }

    override suspend fun renameSession(id: String, newName: String?) {
        session(id).first()?.let { session ->
            val renamedSession = session.copy(name = newName)
            sessionsDataSource.upsertSession(renamedSession)
        }
    }

    override suspend fun addTagToSession(id: String, tag: Tag) {
        session(id).first()?.let { session ->
            val newTags = session.tags.toMutableList().apply { add(tag) }
            val updatedSession = session.copy(tags = newTags)
            sessionsDataSource.upsertSession(updatedSession)
        }
    }

    override suspend fun removeTagFromSession(id: String, tagId: String) {
        session(id).first()?.let { session ->
            val newTags = session.tags.toMutableList().filterNot { it.id == tagId }
            val updatedSession = session.copy(tags = newTags)
            sessionsDataSource.upsertSession(updatedSession)
        }
    }

    override suspend fun removeAllTagsFromSession(id: String) {
        session(id).first()?.let { session ->
            val updatedSession = session.copy(tags = emptyList())
            sessionsDataSource.upsertSession(updatedSession)
        }
    }

    override suspend fun deleteSession(id: String) {
        sessionsDataSource.deleteSession(id)
    }

    override suspend fun deleteAllSessions() {
        sessionsDataSource.clearSessions()
    }
}