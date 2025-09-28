package com.example.space_timetagger.database.data

import com.example.space_timetagger.database.domain.SessionsDao
import com.example.space_timetagger.sessions.domain.datasource.SessionId
import com.example.space_timetagger.sessions.domain.datasource.SessionsDataSource
import com.example.space_timetagger.sessions.domain.models.Session
import com.example.space_timetagger.sessions.domain.models.Tag
import com.example.space_timetagger.sessions.domain.models.copyAndSortTags
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomSessionsDataSource(
    private val sessionsDao: SessionsDao,
    private val ioDispatcher: CoroutineDispatcher
) : SessionsDataSource {
    override fun getSessions(): Flow<List<Session>> =
        sessionsDao.getSessionsWithTags().map {
            it.map(SessionWithTags::toSession)
                .map(Session::copyAndSortTags)
        }

    override fun getSession(id: SessionId): Flow<Session> =
        sessionsDao.getSessionWithTags(id)
            .map(SessionWithTags::toSession)
            .map(Session::copyAndSortTags)

    override suspend fun upsertSessionWithoutTags(session: Session) =
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

    override suspend fun upsertTag(sessionId: String, tag: Tag) =
        withContext(ioDispatcher) {
            sessionsDao.upsertTag(tag.toEntity(sessionId))
        }

    override suspend fun deleteTag(id: String) =
        withContext(ioDispatcher) {
            sessionsDao.deleteTag(id)
        }

    override suspend fun clearTags(sessionId: String) =
        withContext(ioDispatcher) {
            sessionsDao.deleteTags(sessionId)
        }
}