package com.example.space_timetagger.sessions.presentation.sessionDetail

import com.example.space_timetagger.sessions.presentation.models.TagUiModel
import java.time.LocalDateTime
import kotlin.random.Random

val noTags = listOf<TagUiModel>()
val someTags = listOf(
    TagUiModel(dateTime = LocalDateTime.now().minusMinutes(8), isArchived = true),
    TagUiModel(dateTime = LocalDateTime.now().minusMinutes(5)),
    TagUiModel(dateTime = LocalDateTime.now().minusSeconds(23)),
)
val seededRandom = Random(46034)
val manyTags = List(20) { i ->
    TagUiModel(
        dateTime = LocalDateTime.now().minusSeconds(2 * i + 3L),
        isArchived = seededRandom.nextInt(2) > 0,
    )
}
