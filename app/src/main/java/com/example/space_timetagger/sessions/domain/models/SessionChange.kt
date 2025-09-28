package com.example.space_timetagger.sessions.domain.models

// possibly rename change -> action in the future
sealed interface SessionChange {
    data object Rename : SessionChange
    data class AddTag(val id: String) : SessionChange
    data class DeleteTag(val id: String) : SessionChange
    data object ClearTags : SessionChange
}