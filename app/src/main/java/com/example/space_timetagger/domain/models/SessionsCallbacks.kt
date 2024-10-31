package com.example.space_timetagger.domain.models

interface SessionsCallbacks {
    fun new(name: String? = null)
    fun delete(id: String)
    fun deleteAll()
}