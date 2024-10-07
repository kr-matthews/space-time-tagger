package com.example.space_timetagger.domain.repository

import com.example.space_timetagger.domain.model.SessionModel
import kotlinx.coroutines.flow.Flow

interface SessionsRepository {
    suspend fun sessionIds(): Flow<List<String>>
    suspend fun session(id: String): Flow<SessionModel?>
    suspend fun newSession(name: String? = null): String
    suspend fun updateSession(session: SessionModel)
    suspend fun deleteSession(id: String)
    suspend fun deleteAllSessions()
}