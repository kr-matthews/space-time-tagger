package com.example.space_timetagger.sessions.presentation.settings

sealed interface SettingsEvent {
    data object TapBack : SettingsEvent
    data class TapLocationTaggingToggle(
        val hasLocationPermission: Boolean,
    ) : SettingsEvent

    data object LocationPermissionRequestLaunched : SettingsEvent
    data object LocationPermissionGranted : SettingsEvent
    data object LocationPermissionDenied : SettingsEvent
    data object LocationPermissionDialogDismissed : SettingsEvent
}