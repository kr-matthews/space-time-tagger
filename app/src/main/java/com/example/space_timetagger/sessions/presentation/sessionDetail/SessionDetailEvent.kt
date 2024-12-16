package com.example.space_timetagger.sessions.presentation.sessionDetail

import java.time.OffsetDateTime

sealed interface SessionDetailEvent {
    data object TapName : SessionDetailEvent
    data object TapNameDoneEditing : SessionDetailEvent
    data class ChangeName(val newName: String?) : SessionDetailEvent
    data class TapNewTagButton(val time: OffsetDateTime) : SessionDetailEvent
    data class TapConfirmDeleteTag(val tagId: String) : SessionDetailEvent
    data object TapConfirmDeleteAllTags : SessionDetailEvent
}