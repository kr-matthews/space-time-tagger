package com.example.space_timetagger.domain.model

interface SessionCallbacks {
    fun addTag()
    fun deleteTag(tag: TagModel)
    fun clearTags()
}