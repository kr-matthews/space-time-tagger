package com.example.space_timetagger.sessions.presentation.settings

sealed interface SettingsViewState {
    data object Loading : SettingsViewState
    data class Success(
        val taggingLocationIsEnabled: Boolean,
        val locationPermissionMustBeRequested: Boolean = false,
        val locationPermissionExplanationIsVisible: Boolean = false,
    ) : SettingsViewState
}
