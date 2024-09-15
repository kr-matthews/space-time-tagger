package com.example.space_timetagger.ui.taglist

import androidx.lifecycle.ViewModel
import com.example.space_timetagger.domain.model.TagListCallbacks
import com.example.space_timetagger.domain.model.TagModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TagListViewModel : ViewModel() {
    private val _tagList = MutableStateFlow<List<TagModel>>(listOf())
    val tagList = _tagList.asStateFlow()


    val callbacks = object : TagListCallbacks {
        override fun addTag(tag: TagModel) {
            _tagList.update { it.toMutableList().apply { add(tag) } }
        }

        override fun deleteTag(tag: TagModel) {
            _tagList.update { it.toMutableList().apply { remove(tag) } }
        }

        override fun deleteAllTags() {
            _tagList.update { listOf() }
        }

    }
}