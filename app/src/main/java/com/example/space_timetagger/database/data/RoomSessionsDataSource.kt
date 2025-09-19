package com.example.space_timetagger.database.data

import com.example.space_timetagger.database.domain.SessionsDao
import com.example.space_timetagger.sessions.domain.datasource.SessionId
import com.example.space_timetagger.sessions.domain.datasource.SessionsDataSource
import com.example.space_timetagger.sessions.domain.models.Session
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomSessionsDataSource(
    private val sessionsDao: SessionsDao,
    private val ioDispatcher: CoroutineDispatcher
) : SessionsDataSource {
    override fun getSessions(): Flow<List<Session>> =
        sessionsDao.getSessionsWithTags().map { it.map(SessionWithTags::toSession) }

    override fun getSession(id: SessionId): Flow<Session> =
        sessionsDao.getSessionWithTags(id).map(SessionWithTags::toSession)

    override suspend fun upsertSession(session: Session) =
        withContext(ioDispatcher) {
            sessionsDao.upsertSessionWithTags(session.toSessionWithTags())
        }

    override suspend fun deleteSession(id: SessionId) =
        withContext(ioDispatcher) {
            sessionsDao.deleteSession(id)
        }

    override suspend fun clearSessions() =
        withContext(ioDispatcher) {
            sessionsDao.clearSessions()
        }
}