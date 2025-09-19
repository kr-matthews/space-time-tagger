package com.example.space_timetagger.sessions.domain

import com.example.space_timetagger.sessions.domain.models.Session
import com.example.space_timetagger.sessions.domain.models.Tag
import java.time.LocalDateTime
import kotlin.random.Random


val mockDateTime: LocalDateTime = LocalDateTime.of(2024, 7, 28, 16, 0, 58, 0)
val mockTags = List(13) { i ->
    Tag(
        dateTime = mockDateTime.plusSeconds(Random.nextInt(i * i, (i + 1) * (i + 1)).toLong())
    )
}
val mockTag = mockTags[0]
val mockSession = Session(name = "Test Session", tags = mockTags)
val mockSession2 = Session(
    name = "2nd session",
    tags = mockTags.map { Tag(dateTime = it.dateTime.plusDays(2).plusSeconds(94)) },
)
val mockSessions = listOf(mockSession, mockSession2)