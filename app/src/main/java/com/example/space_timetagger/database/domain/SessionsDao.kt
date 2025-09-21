package com.example.space_timetagger.database.domain

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.space_timetagger.database.data.SessionEntity
import com.example.space_timetagger.database.data.SessionWithTags
import com.example.space_timetagger.database.data.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionsDao {
    @Transaction
    @Query("SELECT * FROM sessions")
    fun getSessionsWithTags(): Flow<List<SessionWithTags>>

    @Transaction
    @Query("SELECT * FROM sessions WHERE id=:id")
    fun getSessionWithTags(id: String): Flow<SessionWithTags>

    @Upsert
    fun upsertSession(sessionEntity: SessionEntity)

    @Upsert
    fun upsertTag(tagEntity: TagEntity)

    @Query("DELETE FROM sessions WHERE id=:id")
    fun deleteSession(id: String)

    @Query("DELETE FROM sessions")
    fun clearSessions()

    @Query("DELETE FROM tags WHERE id=:id")
    fun deleteTag(id: String)

    @Query("DELETE FROM tags WHERE sessionId=:sessionId")
    fun deleteTags(sessionId: String)
}