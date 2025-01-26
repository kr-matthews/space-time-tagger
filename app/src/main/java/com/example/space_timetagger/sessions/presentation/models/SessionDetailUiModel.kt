package com.example.space_timetagger.sessions.presentation.models

import java.util.UUID

data class SessionDetailUiModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String?,
    val nameIsBeingEdited: Boolean,
    val tags: List<TagUiModel>,
    val deleteAllIsEnabled: Boolean,
    val tapAnywhereIsEnabled: Boolean,
)
