package com.example.space_timetagger.database.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.space_timetagger.sessions.domain.models.Session
import com.example.space_timetagger.sessions.domain.models.Tag
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val id: String,
    val name: String?,
    val timeOffsetSeconds: Long?,
)

fun Session.toEntity() = SessionEntity(
    id = id,
    name = name,
    timeOffsetSeconds = timeOffset?.inWholeSeconds,
)

fun SessionEntity.toSession(tags: List<Tag>) = Session(
    id = id,
    name = name,
    timeOffset = timeOffsetSeconds?.toDuration(DurationUnit.SECONDS),
    tags = tags,
)
