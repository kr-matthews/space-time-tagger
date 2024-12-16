package com.example.space_timetagger.sessions.presentation.sessionDetail

import com.example.space_timetagger.sessions.presentation.models.TagUiModel
import java.time.OffsetDateTime

val tag = TagUiModel(dateTime = OffsetDateTime.now().minusSeconds(23))

val noTags = listOf<TagUiModel>()
val someTags = listOf(
    TagUiModel(dateTime = OffsetDateTime.now().minusMinutes(8)),
    TagUiModel(dateTime = OffsetDateTime.now().minusMinutes(5)),
    TagUiModel(dateTime = OffsetDateTime.now().minusSeconds(23)),
)
val manyTags = List(20) { i ->
    TagUiModel(dateTime = OffsetDateTime.now().minusSeconds(2 * i + 3L))
}
