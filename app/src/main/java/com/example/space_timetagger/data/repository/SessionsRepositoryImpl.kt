package com.example.space_timetagger.data.repository

import com.example.space_timetagger.domain.model.SessionModel
import com.example.space_timetagger.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class SessionsRepositoryImpl : SessionsRepository {
    private val sessions = MutableStateFlow(mutableListOf<SessionModel>())

    override suspend fun sessionIds(): Flow<List<String>> =
        sessions.map { list -> list.map { session -> session.id } }

    override suspend fun session(id: String): Flow<SessionModel?> =
        sessions.map { list -> list.find { session -> session.id == id } }

    override suspend fun newSession(name: String?): String {
        val newSession = SessionModel(name)
        sessions.update { it.apply { add(newSession) } }
        return newSession.id
    }

    // provide more precise update functions?
    override suspend fun updateSession(session: SessionModel) {
        sessions.update { list ->
            list.apply {
                val index = list.indexOfFirst { it.id == session.id }
                if (index > -1) {
                    list[index] = session
                }
            }
        }
    }

    override suspend fun deleteSession(id: String) {
        sessions.update { list -> list.apply { removeIf { it.id == id } } }
    }

    override suspend fun deleteAllSessions() {
        sessions.update { mutableListOf() }
    }
}