package com.example.space_timetagger.sessions.domain.repository

import com.example.space_timetagger.sessions.domain.models.Session
import com.example.space_timetagger.sessions.domain.models.Tag
import kotlinx.coroutines.flow.Flow

interface SessionsRepository {
    fun sessions(): Flow<List<Session>>
    fun session(id: String): Flow<Session?>
    suspend fun newSession(name: String? = null): String
    suspend fun renameSession(id: String, newName: String?)
    suspend fun addTagToSession(id: String, tag: Tag)
    suspend fun removeTagFromSession(id: String, tagId: String)
    suspend fun removeAllTagsFromSession(id: String)
    suspend fun deleteSession(id: String)
    suspend fun deleteAllSessions()
}