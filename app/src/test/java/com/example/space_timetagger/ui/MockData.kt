package com.example.space_timetagger.ui

import com.example.space_timetagger.domain.model.SessionModel
import com.example.space_timetagger.domain.model.TagModel
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.random.Random


val mockDateTime: OffsetDateTime = OffsetDateTime.of(2024, 7, 28, 16, 0, 58, 0, ZoneOffset.UTC)
val mockTags = List(13) { i ->
    TagModel(
        mockDateTime.plusSeconds(Random.nextInt(i * i, (i + 1) * (i + 1)).toLong())
    )
}
val mockTag = mockTags[0]
val mockSession = SessionModel(name = "Test Session", tags = mockTags)
val mockSession2 = SessionModel(
    name = "2nd session",
    tags = mockTags.map { TagModel(it.dateTime.plusDays(2).plusSeconds(94)) },
)
val mockSessions = listOf(mockSession, mockSession2)