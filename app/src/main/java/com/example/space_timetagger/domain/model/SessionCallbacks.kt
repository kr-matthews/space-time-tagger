package com.example.space_timetagger.domain.model

interface SessionCallbacks {
    fun setName(name: String?)
    fun addTag()
    fun deleteTag(tag: TagModel)
    fun clearTags()
}