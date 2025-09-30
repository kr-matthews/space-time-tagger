package com.example.space_timetagger.sessions.presentation.models

import com.example.space_timetagger.sessions.domain.models.Tag
import java.time.LocalDateTime
import java.util.UUID

data class TagUiModel(
    val id: String = UUID.randomUUID().toString(),
    val dateTime: LocalDateTime,
    val isArchived: Boolean = false,
)

fun Tag.toUiModel() = TagUiModel(id, dateTime, isArchived)