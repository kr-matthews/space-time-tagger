package com.example.space_timetagger.sessions.presentation.models

import com.example.space_timetagger.sessions.domain.models.Tag
import java.time.LocalDateTime
import java.util.UUID
import kotlin.time.Duration

data class TagUiModel(
    val id: String = UUID.randomUUID().toString(),
    val dateTime: LocalDateTime,
    val isArchived: Boolean = false,
)

fun Tag.toUiModel(timeOffset: Duration = Duration.ZERO) =
    TagUiModel(id, dateTime.plusSeconds(timeOffset.inWholeSeconds), isArchived)