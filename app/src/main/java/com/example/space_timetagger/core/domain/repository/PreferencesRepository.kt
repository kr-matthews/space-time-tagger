package com.example.space_timetagger.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val taggingLocationIsEnabled: Flow<Boolean>
    suspend fun enableTaggingLocation()
    suspend fun disableTaggingLocation()
}