package com.example.space_timetagger.sessions.data.repository

import com.example.space_timetagger.sessions.domain.datasource.SessionsDataSource
import com.example.space_timetagger.sessions.domain.models.Session
import com.example.space_timetagger.sessions.domain.models.SessionChange
import com.example.space_timetagger.sessions.domain.models.SessionsChange
import com.example.space_timetagger.sessions.domain.models.Tag
import com.example.space_timetagger.sessions.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class SessionsRepositoryImpl(
    private val sessionsDataSource: SessionsDataSource,
) : SessionsRepository {
    private val lastSessionsChange: MutableStateFlow<SessionsChange?> = MutableStateFlow(null)

    override fun sessions() = sessionsDataSource.getSessions()

    override fun sessionsAndLastChange(): Flow<Pair<List<Session>, SessionsChange?>> =
        combine(sessions(), lastSessionsChange) { sessions, sessionsChange ->
            Pair(sessions, sessionsChange)
        }

    override fun session(id: String): Flow<Session?> = sessionsDataSource.getSession(id)

    // TODO: fix last SessionChange
    override fun sessionAndLastChange(id: String): Flow<Pair<Session, SessionChange>?> =
        session(id).map { it?.let { Pair(it, SessionChange.Clear) } }

    override suspend fun newSession(name: String?): String {
        val newSession = Session(name = name)
        sessionsDataSource.upsertSessionWithoutTags(newSession)
        lastSessionsChange.update { SessionsChange.Create(newSession.id) }
        // TODO: remove return value (?)
        return newSession.id
    }

    override suspend fun renameSession(id: String, newName: String?) {
        session(id).first()?.let { session ->
            val renamedSession = session.copy(name = newName)
            sessionsDataSource.upsertSessionWithoutTags(renamedSession)
            lastSessionsChange.update { SessionsChange.Edit(id) }
        }
    }

    override suspend fun addTagToSession(sessionId: String, tag: Tag) {
        sessionsDataSource.upsertTag(sessionId, tag)
        lastSessionsChange.update { SessionsChange.Edit(sessionId) }
    }

    override suspend fun removeTag(sessionId: String, tagId: String) {
        sessionsDataSource.deleteTag(tagId)
        lastSessionsChange.update { SessionsChange.Edit(sessionId) }
    }

    override suspend fun removeAllTagsFromSession(id: String) {
        sessionsDataSource.clearTags(id)
        lastSessionsChange.update { SessionsChange.Edit(id) }
    }

    override suspend fun deleteSession(id: String) {
        sessionsDataSource.deleteSession(id)
        lastSessionsChange.update { SessionsChange.Delete(id) }
    }

    override suspend fun deleteAllSessions() {
        sessionsDataSource.clearSessions()
        lastSessionsChange.update { SessionsChange.Clear }
    }
}