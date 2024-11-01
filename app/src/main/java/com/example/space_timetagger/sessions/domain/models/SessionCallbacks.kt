package com.example.space_timetagger.sessions.domain.models

import java.time.OffsetDateTime

interface SessionCallbacks {
    fun setName(name: String?)
    fun addTag(now: OffsetDateTime = OffsetDateTime.now())
    fun deleteTag(id: String)
    fun deleteAllTags()
}