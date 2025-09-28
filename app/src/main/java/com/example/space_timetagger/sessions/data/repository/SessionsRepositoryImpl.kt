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
import kotlinx.coroutines.flow.update

class SessionsRepositoryImpl(
    private val sessionsDataSource: SessionsDataSource,
) : SessionsRepository {
    private val lastSessionsChange: MutableStateFlow<SessionsChange?> = MutableStateFlow(null)

    private val lastSessionChange: MutableStateFlow<Pair<String, SessionChange>?> =
        MutableStateFlow(null)

    override fun sessions() = sessionsDataSource.getSessions()

    override fun sessionsAndLastChange(): Flow<Pair<List<Session>, SessionsChange?>> =
        combine(sessions(), lastSessionsChange) { sessions, sessionsChange ->
            Pair(sessions, sessionsChange)
        }

    override fun session(id: String): Flow<Session?> = sessionsDataSource.getSession(id)

    override fun sessionAndLastChange(id: String): Flow<Pair<Session, SessionChange?>?> =
        combine(session(id), lastSessionChange) { session, sessionChange ->
            session?.let {
                if (sessionChange?.first == id) {
                    Pair(it, sessionChange.second)
                } else {
                    Pair(it, null)
                }
            }
        }

    override suspend fun newSession(name: String?): String {
        val newSession = Session(name = name)
        sessionsDataSource.upsertSessionWithoutTags(newSession)
        lastSessionsChange.update { SessionsChange.Create(newSession.id) }
        lastSessionChange.update { Pair(newSession.id, SessionChange.Create) }
        // TODO: remove return value (?)
        return newSession.id
    }

    override suspend fun renameSession(id: String, newName: String?) {
        session(id).first()?.let { session ->
            val renamedSession = session.copy(name = newName)
            sessionsDataSource.upsertSessionWithoutTags(renamedSession)
            lastSessionsChange.update { SessionsChange.Edit(id) }
            lastSessionChange.update { Pair(id, SessionChange.Rename) }
        }
    }

    override suspend fun addTagToSession(sessionId: String, tag: Tag) {
        sessionsDataSource.upsertTag(sessionId, tag)
        lastSessionsChange.update { SessionsChange.Edit(sessionId) }
        lastSessionChange.update { Pair(sessionId, SessionChange.AddTag(tag.id)) }
    }

    override suspend fun removeTag(sessionId: String, tagId: String) {
        sessionsDataSource.deleteTag(tagId)
        lastSessionsChange.update { SessionsChange.Edit(sessionId) }
        lastSessionChange.update { Pair(sessionId, SessionChange.DeleteTag(tagId)) }
    }

    override suspend fun removeAllTagsFromSession(id: String) {
        sessionsDataSource.clearTags(id)
        lastSessionsChange.update { SessionsChange.Edit(id) }
        lastSessionChange.update { Pair(id, SessionChange.ClearTags) }
    }

    override suspend fun deleteSession(id: String) {
        sessionsDataSource.deleteSession(id)
        lastSessionsChange.update { SessionsChange.Delete(id) }
        lastSessionChange.update { Pair(id, SessionChange.Delete) }
    }

    override suspend fun deleteAllSessions() {
        sessionsDataSource.clearSessions()
        lastSessionsChange.update { SessionsChange.Clear }
    }
}