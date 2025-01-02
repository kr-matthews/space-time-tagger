package com.example.space_timetagger.sessions.presentation.settings

sealed interface SettingsEvent {
    data object TapBack : SettingsEvent
    data object TapLocationTaggingToggle : SettingsEvent
}