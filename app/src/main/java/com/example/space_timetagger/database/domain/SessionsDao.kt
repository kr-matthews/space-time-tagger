package com.example.space_timetagger.database.domain

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.space_timetagger.database.data.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionsDao {
    @Query("SELECT * FROM sessions")
    fun getSessions(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE id LIKE :id")
    fun getSession(id: String): Flow<SessionEntity>

    @Upsert
    fun upsertSession(session: SessionEntity)

    @Query("DELETE FROM sessions WHERE id=:id")
    fun deleteSession(id: String)

    @Query("DELETE FROM sessions")
    fun clearSessions()
}