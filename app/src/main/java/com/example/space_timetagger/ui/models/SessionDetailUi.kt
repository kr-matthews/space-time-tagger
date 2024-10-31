package com.example.space_timetagger.ui.models

import com.example.space_timetagger.domain.models.Session
import java.util.UUID

data class SessionDetailUi(
    val id: String = UUID.randomUUID().toString(),
    val name: String?,
    val tags: List<TagUi>,
)

fun Session.toDetailUiModel() = SessionDetailUi(id, name, tags.map { it.toUiModel() })
