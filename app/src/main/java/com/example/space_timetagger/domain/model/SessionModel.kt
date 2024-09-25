package com.example.space_timetagger.domain.model

import java.util.UUID

data class SessionModel(
    val name: String? = null,
    val id: String = UUID.randomUUID().toString()
)
