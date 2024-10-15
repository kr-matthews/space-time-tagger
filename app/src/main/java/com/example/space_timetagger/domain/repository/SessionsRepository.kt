package com.example.space_timetagger.domain.repository

import com.example.space_timetagger.domain.model.SessionModel
import com.example.space_timetagger.domain.model.TagModel
import kotlinx.coroutines.flow.Flow

interface SessionsRepository {
    fun sessions(): Flow<List<SessionModel>>
    fun session(id: String): Flow<SessionModel?>
    suspend fun newSession(name: String? = null): String
    suspend fun renameSession(id: String, newName: String?)
    suspend fun addTagToSession(id: String, tag: TagModel)
    suspend fun removeTagFromSession(id: String, tag: TagModel)
    suspend fun removeAllTagsFromSession(id: String)
    suspend fun deleteSession(id: String)
    suspend fun deleteAllSessions()
}