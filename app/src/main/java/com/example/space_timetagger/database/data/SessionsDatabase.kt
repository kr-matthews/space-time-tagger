package com.example.space_timetagger.database.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.space_timetagger.database.domain.SessionsDao

//@Database(entities = [SessionEntity::class, TagEntity::class], version = 1)
@Database(entities = [SessionEntity::class], version = 1)
abstract class SessionsDatabase : RoomDatabase() {
    abstract val sessionsDao: SessionsDao
}