package com.example.space_timetagger.sessions.presentation.sessionDetail

import java.time.OffsetDateTime

sealed interface SessionDetailEvent {
    data object TapBack : SessionDetailEvent
    data object TapSettings : SessionDetailEvent
    data object TapEdit : SessionDetailEvent
    data class ConfirmNameEdit(val newName: String) : SessionDetailEvent
    data object CancelNameEdit : SessionDetailEvent
    data class TapNewTagButton(val time: OffsetDateTime) : SessionDetailEvent
    data class TapConfirmDeleteTag(val tagId: String) : SessionDetailEvent
    data object TapConfirmDeleteAllTags : SessionDetailEvent
    data class TapAnywhere(val time: OffsetDateTime) : SessionDetailEvent
    data class AutoScrollToTag(val id: String) : SessionDetailEvent
}