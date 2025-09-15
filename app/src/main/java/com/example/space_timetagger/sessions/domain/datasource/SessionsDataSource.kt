package com.example.space_timetagger.sessions.domain.datasource

import com.example.space_timetagger.sessions.domain.models.Session
import kotlinx.coroutines.flow.Flow

typealias SessionId = String

interface SessionsDataSource {
    fun getSessions(): Flow<List<Session>>

    fun getSession(id: SessionId): Flow<Session>

    suspend fun upsertSession(session: Session)

    suspend fun deleteSession(id: SessionId)

    suspend fun clearSessions()
}