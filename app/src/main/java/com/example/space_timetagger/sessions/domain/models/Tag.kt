package com.example.space_timetagger.sessions.domain.models

import java.time.OffsetDateTime
import java.util.UUID

data class Tag(
    val id: String = UUID.randomUUID().toString(),
    val dateTime: OffsetDateTime,
)
