package com.example.space_timetagger.sessions.presentation.models

import java.util.UUID
import kotlin.time.Duration

data class SessionDetailUiModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String?,
    val timeOffset: Duration?,
    val nameIsBeingEdited: Boolean,
    val timeOffsetIsBeingEdited: Boolean,
    val tags: List<TagUiModel>,
    val tagIdToScrollTo: String?,
    val deleteAllIsEnabled: Boolean,
    val tapAnywhereIsEnabled: Boolean,
    val progress: Float? = null,
)
