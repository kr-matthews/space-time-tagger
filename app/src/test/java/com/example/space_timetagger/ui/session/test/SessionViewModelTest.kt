package com.example.space_timetagger.ui.session.test

import com.example.space_timetagger.domain.model.TagModel
import com.example.space_timetagger.ui.session.SessionViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.OffsetDateTime

class SessionViewModelTest {
    private val viewModel = SessionViewModel()

    @Test
    fun initially_hasNoName() {
        val initialName = viewModel.name.value
        assertNull(initialName)
    }

    @Test
    fun initially_hasNoTags() {
        val initialTags = viewModel.tags.value
        assert(initialTags.isEmpty())
    }

    @Test
    fun setNameCallback_updatesName() {
        val newName = "Test Session Name"
        viewModel.callbacks.setName(newName)
        val updatedName = viewModel.name.value
        assertEquals(newName, updatedName)
    }

    @Test
    fun addTagCallback_addsANewTag() {
        val tag0Time = OffsetDateTime.now()
        val tag1Time = tag0Time.plusSeconds(7)
        val tag2Time = tag0Time.plusSeconds(8)

        viewModel.callbacks.addTag(tag0Time)
        val tagsAfterFirstTag = viewModel.tags.value
        assertEquals(1, tagsAfterFirstTag.size)
        assertEquals(tag0Time, tagsAfterFirstTag[0].dateTime)

        viewModel.callbacks.addTag(tag1Time)
        viewModel.callbacks.addTag(tag2Time)
        val tagsAfterAllTags = viewModel.tags.value
        assertEquals(3, tagsAfterAllTags.size)
        assertEquals(tag1Time, tagsAfterAllTags[1].dateTime)
        assertEquals(tag2Time, tagsAfterAllTags[2].dateTime)
    }

    @Test
    fun deleteTagCallback_deletesExistingSession() {
        repeat(4) { viewModel.callbacks.addTag() }
        val preDeletionTags = viewModel.tags.value
        val deletedTag = preDeletionTags[2]
        viewModel.callbacks.deleteTag(deletedTag)
        val postDeletionTags = viewModel.tags.value
        assertEquals(preDeletionTags.size - 1, postDeletionTags.size)
        assertEquals(
            preDeletionTags.toMutableList().apply { remove(deletedTag) }.toList(),
            postDeletionTags,
        )
    }

    @Test
    fun deleteTagCallback_doesNothingWithInvalidTag() {
        val now = OffsetDateTime.now()
        repeat(4) { viewModel.callbacks.addTag(now.plusSeconds(it.toLong())) }
        val preDeletionTags = viewModel.tags.value
        val unusedTag = TagModel(now.plusSeconds(preDeletionTags.size.toLong()))
        viewModel.callbacks.deleteTag(unusedTag)
        val postDeletionTags = viewModel.tags.value
        assertEquals(preDeletionTags, postDeletionTags)
    }

    @Test
    fun clearTagsCallback_deletesAllTags() {
        repeat(4) { viewModel.callbacks.addTag() }
        viewModel.callbacks.clearTags()
        val postDeletionTags = viewModel.tags.value
        assert(postDeletionTags.isEmpty())
    }
}
