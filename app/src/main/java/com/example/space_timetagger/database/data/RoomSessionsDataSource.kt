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
        sessionsDao.getSessions().map { it.map(SessionEntity::toSession) }

    override fun getSession(id: SessionId): Flow<Session> =
        sessionsDao.getSession(id).map(SessionEntity::toSession)

    override suspend fun upsertSession(session: Session) =
        withContext(ioDispatcher) {
            sessionsDao.upsertSession(session.toEntity())
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