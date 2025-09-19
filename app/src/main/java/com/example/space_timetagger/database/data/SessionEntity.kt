package com.example.space_timetagger.database.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.space_timetagger.sessions.domain.models.Session
import com.example.space_timetagger.sessions.domain.models.Tag

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val id: String,
    val name: String?,
)

fun Session.toEntity() = SessionEntity(
    id = id,
    name = name,
)

fun SessionEntity.toSession(tags: List<Tag>) = Session(
    id = id,
    name = name,
    tags = tags,
)
