package com.example.space_timetagger.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val keepScreenOnIsEnabled: Flow<Boolean>
    suspend fun enableKeepScreenOn()
    suspend fun disableKeepScreenOn()
    val taggingLocationIsEnabled: Flow<Boolean>
    suspend fun enableTaggingLocation()
    suspend fun disableTaggingLocation()
}