package com.example.space_timetagger.core.domain.repository

import com.example.space_timetagger.sessions.domain.models.SessionNameStrategy
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val keepScreenOnIsEnabled: Flow<Boolean>
    suspend fun enableKeepScreenOn()
    suspend fun disableKeepScreenOn()
    val taggingLocationIsEnabled: Flow<Boolean>
    suspend fun enableTaggingLocation()
    suspend fun disableTaggingLocation()
    val tapAnywhereIsEnabled: Flow<Boolean>
    suspend fun enableTapAnywhere()
    suspend fun disableTapAnywhere()
    val sessionNameStrategy: Flow<SessionNameStrategy>
    suspend fun setSessionNameStrategy(strategy: SessionNameStrategy)
}