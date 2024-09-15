package com.example.space_timetagger.domain.model

interface TagSessionCallbacks {
    fun addTag()
    fun deleteTag(tag: TagModel)
    fun clear()
}