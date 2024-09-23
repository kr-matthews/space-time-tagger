package com.example.space_timetagger.domain.model

interface SessionsCallbacks {
    fun new(name: String? = null)
    fun delete(id: String)
    fun clearAll()
}