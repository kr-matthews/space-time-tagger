package com.example.space_timetagger.sessions.presentation.settings

import com.example.space_timetagger.sessions.domain.models.SessionNameStrategy
import com.example.space_timetagger.sessions.domain.models.defaultSessionNameStrategy

sealed interface SettingsViewState {
    data object Loading : SettingsViewState
    data class Success(
        val keepScreenOnIsEnabled: Boolean,
        val taggingLocationIsEnabled: Boolean,
        val tapAnywhereIsEnabled: Boolean,
        val sessionNameStrategy: SessionNameStrategy = defaultSessionNameStrategy,
        val locationPermissionMustBeRequested: Boolean = false,
        val locationPermissionExplanationIsVisible: Boolean = false,
    ) : SettingsViewState
}
