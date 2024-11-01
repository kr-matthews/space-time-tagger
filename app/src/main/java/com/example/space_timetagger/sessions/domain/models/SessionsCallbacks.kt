package com.example.space_timetagger.sessions.domain.models

interface SessionsCallbacks {
    fun new(name: String? = null)
    fun delete(id: String)
    fun deleteAll()
}