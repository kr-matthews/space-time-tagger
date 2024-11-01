package com.example.space_timetagger.sessions.presentation.models

import com.example.space_timetagger.sessions.domain.models.Session
import java.util.UUID

data class SessionOverviewUi(
    val id: String = UUID.randomUUID().toString(),
    val name: String? = null,
)

fun Session.toOverviewUiModel() = SessionOverviewUi(id, name)
