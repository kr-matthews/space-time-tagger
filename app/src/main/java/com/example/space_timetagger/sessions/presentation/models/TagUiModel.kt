package com.example.space_timetagger.sessions.presentation.models

import com.example.space_timetagger.sessions.domain.models.Tag
import java.time.OffsetDateTime
import java.util.UUID

data class TagUiModel(
    val id: String = UUID.randomUUID().toString(),
    val dateTime: OffsetDateTime,
)

fun Tag.toUiModel() = TagUiModel(id, dateTime)