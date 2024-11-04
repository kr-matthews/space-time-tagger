package com.example.space_timetagger.sessions.presentation.sessionDetail

import com.example.space_timetagger.sessions.domain.models.SessionCallbacks
import com.example.space_timetagger.sessions.presentation.models.TagUi
import java.time.OffsetDateTime

@Suppress("EmptyFunctionBlock")
val dummySessionCallbacks = object : SessionCallbacks {
    override fun setName(name: String?) {}
    override fun addTag(now: OffsetDateTime) {}
    override fun deleteTag(id: String) {}
    override fun deleteAllTags() {}
}

val tag = TagUi(dateTime = OffsetDateTime.now().minusSeconds(23))

val noTags = listOf<TagUi>()
val someTags = listOf(
    TagUi(dateTime = OffsetDateTime.now().minusMinutes(8)),
    TagUi(dateTime = OffsetDateTime.now().minusMinutes(5)),
    TagUi(dateTime = OffsetDateTime.now().minusSeconds(23)),
)
val manyTags = List(20) { i ->
    TagUi(dateTime = OffsetDateTime.now().minusSeconds(2 * i + 3L))
}
