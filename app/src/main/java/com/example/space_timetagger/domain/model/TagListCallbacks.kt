package com.example.space_timetagger.domain.model

interface TagListCallbacks {
    fun addTag(tag: TagModel)
    fun deleteTag(tag: TagModel)
    fun deleteAllTags()
}