package com.example.space_timetagger.domain.model

interface TagSessionCallbacks {
    fun addTag(tag: TagModel)
    fun deleteTag(tag: TagModel)
    fun clear()
}