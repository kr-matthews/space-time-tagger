package com.example.space_timetagger.database.data

import androidx.room.Embedded
import androidx.room.Relation

data class SessionWithTags(
    @Embedded
    val sessionEntity: SessionEntity,
    @Relation(parentColumn = "id", entityColumn = "sessionId")
    val tagEntities: List<TagEntity>,
)

fun SessionWithTags.toSession() = sessionEntity.toSession(
    tagEntities.map(TagEntity::toTag)
)