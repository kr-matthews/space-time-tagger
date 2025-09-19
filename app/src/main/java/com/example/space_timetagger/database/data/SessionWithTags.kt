package com.example.space_timetagger.database.data

import androidx.room.Embedded
import androidx.room.Relation
import com.example.space_timetagger.sessions.domain.models.Session

data class SessionWithTags(
    @Embedded
    val sessionEntity: SessionEntity,
    @Relation(parentColumn = "id", entityColumn = "sessionId")
    val tagEntities: List<TagEntity>,
)

fun Session.toSessionWithTags() = SessionWithTags(
    sessionEntity = toEntity(),
    tagEntities = tags.map { it.toEntity(id) },
)

fun SessionWithTags.toSession() = sessionEntity.toSession(
    tagEntities.map(TagEntity::toTag)
)