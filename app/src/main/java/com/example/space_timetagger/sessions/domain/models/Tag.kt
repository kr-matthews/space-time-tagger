package com.example.space_timetagger.sessions.domain.models

import com.example.space_timetagger.location.domain.models.LatLng
import java.time.OffsetDateTime
import java.util.UUID

data class Tag(
    val id: String = UUID.randomUUID().toString(),
    val dateTime: OffsetDateTime,
    val latLng: LatLng? = null,
)
