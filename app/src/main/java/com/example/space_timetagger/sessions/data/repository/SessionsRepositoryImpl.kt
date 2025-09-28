package com.example.space_timetagger.sessions.data.repository

import com.example.space_timetagger.sessions.domain.datasource.SessionsDataSource
import com.example.space_timetagger.sessions.domain.models.Session
import com.example.space_timetagger.sessions.domain.models.Tag
import com.example.space_timetagger.sessions.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SessionsRepositoryImpl(
    private val sessionsDataSource: SessionsDataSource,
) : SessionsRepository {
    override fun sessions() = sessionsDataSource.getSessions()

    override fun session(id: String): Flow<Session?> = sessionsDataSource.getSession(id)

    override suspend fun newSession(name: String?): String {
        val session = Session(name = name)
        sessionsDataSource.upsertSessionWithoutTags(session)
        return session.id
    }

    override suspend fun renameSession(id: String, newName: String?) {
        session(id).first()?.let { session ->
            val renamedSession = session.copy(name = newName)
            sessionsDataSource.upsertSessionWithoutTags(renamedSession)
        }
    }

    override suspend fun addTagToSession(sessionId: String, tag: Tag) {
        sessionsDataSource.upsertTag(sessionId, tag)
    }

    override suspend fun removeTag(sessionId: String, tagId: String) {
        sessionsDataSource.deleteTag(tagId)
    }

    override suspend fun removeAllTagsFromSession(id: String) {
        sessionsDataSource.clearTags(id)
    }

    override suspend fun deleteSession(id: String) {
        sessionsDataSource.deleteSession(id)
    }

    override suspend fun deleteAllSessions() {
        sessionsDataSource.clearSessions()
    }
}