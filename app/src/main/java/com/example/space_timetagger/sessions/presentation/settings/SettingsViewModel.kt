package com.example.space_timetagger.sessions.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.space_timetagger.App
import com.example.space_timetagger.core.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    private val taggingLocationIsEnabled = preferencesRepository.taggingLocationIsEnabled

    val viewState = taggingLocationIsEnabled.map { taggingLocationIsEnabled ->
        SettingsViewState(
            taggingLocationIsEnabled = taggingLocationIsEnabled,
        )
    }

    fun handleEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.TapBack -> Unit // handled in compose
            SettingsEvent.TapLocationTaggingToggle -> onLocationTaggingToggleTap()
        }
    }

    private fun onLocationTaggingToggleTap() {
        viewModelScope.launch {
            if (taggingLocationIsEnabled.firstOrNull() == true) {
                disableTaggingLocation()
            } else {
                enableTaggingLocation()
            }
        }
    }

    private suspend fun enableTaggingLocation() = preferencesRepository.enableTaggingLocation()

    private suspend fun disableTaggingLocation() = preferencesRepository.disableTaggingLocation()
}

@Suppress("UNCHECKED_CAST")
class SettingsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(App.appModule.preferencesRepository) as T
    }
}
