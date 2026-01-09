package com.example.space_timetagger.sessions.presentation.models

import com.example.space_timetagger.sessions.domain.models.Session
import java.util.UUID

data class SessionOverviewUiModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String? = null,
    val progress: Float? = null,
)

fun Session.toOverviewUiModel() = SessionOverviewUiModel(
    id,
    name,
    with(tags) { if (isEmpty()) null else count { it.isArchived } / size.toFloat() }
)
