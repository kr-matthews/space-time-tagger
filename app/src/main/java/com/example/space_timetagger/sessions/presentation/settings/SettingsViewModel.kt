package com.example.space_timetagger.sessions.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.space_timetagger.App
import com.example.space_timetagger.core.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    private val locationTaggingIsEnabled = preferencesRepository.taggingLocationIsEnabled

    val viewState = locationTaggingIsEnabled.map { locationTaggingIsEnabled ->
        SettingsViewState(
            locationTaggingIsEnabled = locationTaggingIsEnabled,
        )
    }

    fun handleEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.TapBack -> Unit // handled in compose
            SettingsEvent.EnableLocationTagging -> enableTaggingLocation()
            SettingsEvent.DisableLocationTagging -> disableTaggingLocation()
        }
    }

    private fun enableTaggingLocation() {
        viewModelScope.launch {
            preferencesRepository.enableTaggingLocation()
        }
    }

    private fun disableTaggingLocation() {
        viewModelScope.launch {
            preferencesRepository.disableTaggingLocation()
        }
    }
}

@Suppress("UNCHECKED_CAST")
class SettingsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(App.appModule.preferencesRepository) as T
    }
}
