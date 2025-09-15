package com.example.space_timetagger.database.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.space_timetagger.sessions.domain.models.Session

// TODO re-add tags
@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val id: String,
    val name: String?,
//    val tags: List<TagEntity>,
)

fun Session.toEntity() = SessionEntity(
    id = id,
    name = name,
//    tags = tags.map(Tag::toEntity),
)

fun SessionEntity.toSession() = Session(
    id = id,
    name = name,
//    tags = tags.map(TagEntity::toTag),
)
