package com.example.space_timetagger.sessions.domain.models

sealed interface SessionChange {
    data object Create : SessionChange
    data object Rename : SessionChange
    data class AddTag(val id: String) : SessionChange
    data class DeleteTag(val id: String) : SessionChange
    data object ClearTags : SessionChange
    data object Delete : SessionChange
}