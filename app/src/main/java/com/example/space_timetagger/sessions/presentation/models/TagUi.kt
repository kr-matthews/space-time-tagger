package com.example.space_timetagger.sessions.presentation.models

import com.example.space_timetagger.sessions.domain.models.Tag
import java.time.OffsetDateTime
import java.util.UUID

data class TagUi(
    val id: String = UUID.randomUUID().toString(),
    val dateTime: OffsetDateTime,
)

fun Tag.toUiModel() = TagUi(id, dateTime)