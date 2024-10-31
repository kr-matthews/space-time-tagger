package com.example.space_timetagger.domain.models

import java.util.UUID

data class Session(
    val id: String = UUID.randomUUID().toString(),
    val name: String? = null,
    val tags: List<Tag> = listOf(),
)
