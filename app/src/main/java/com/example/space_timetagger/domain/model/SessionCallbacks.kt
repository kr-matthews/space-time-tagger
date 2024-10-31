package com.example.space_timetagger.domain.model

import java.time.OffsetDateTime

interface SessionCallbacks {
    fun setName(name: String?)
    fun addTag(now: OffsetDateTime = OffsetDateTime.now())
    fun deleteTag(tag: TagModel)
    fun deleteAllTags()
}