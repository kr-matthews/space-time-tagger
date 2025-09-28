package com.example.space_timetagger.sessions.domain.datasource

import com.example.space_timetagger.sessions.domain.models.Session
import com.example.space_timetagger.sessions.domain.models.Tag
import kotlinx.coroutines.flow.Flow

typealias SessionId = String
typealias TagId = String

interface SessionsDataSource {
    fun getSessions(): Flow<List<Session>>
    fun getSession(id: SessionId): Flow<Session>
    suspend fun upsertSessionWithoutTags(session: Session)
    suspend fun deleteSession(id: SessionId)
    suspend fun clearSessions()
    suspend fun upsertTag(sessionId: SessionId, tag: Tag)
    suspend fun deleteTag(id: TagId)
    suspend fun clearTags(sessionId: SessionId)
}