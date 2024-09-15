package com.example.space_timetagger.domain.model

import java.util.UUID

interface SessionsCallbacks {
    fun new(name: String? = null)
    fun select(id: UUID?)
    fun delete(id: UUID)
    fun clearAll()
}