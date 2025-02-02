package com.example.space_timetagger.sessions.domain.models

sealed interface SessionsChange {
    data class Create(val id: String) : SessionsChange
    data class Edit(val id: String) : SessionsChange
    data class Delete(val id: String) : SessionsChange
    data object Clear : SessionsChange
}