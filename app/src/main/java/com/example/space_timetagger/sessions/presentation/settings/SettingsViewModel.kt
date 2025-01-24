package com.example.space_timetagger.sessions.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.space_timetagger.App
import com.example.space_timetagger.core.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    private val keepScreenOnIsEnabled = preferencesRepository.keepScreenOnIsEnabled
    private val taggingLocationIsEnabled = preferencesRepository.taggingLocationIsEnabled
    private val locationPermissionMustBeRequested = MutableStateFlow(false)
    private val locationPermissionExplanationIsVisible = MutableStateFlow(false)

    val viewState = combine(
        keepScreenOnIsEnabled,
        taggingLocationIsEnabled,
        locationPermissionMustBeRequested,
        locationPermissionExplanationIsVisible,
    ) { keepScreenOnIsEnabled, taggingLocationIsEnabled, locationPermissionMustBeRequested, locationPermissionExplanationIsVisible ->
        SettingsViewState.Success(
            keepScreenOnIsEnabled = keepScreenOnIsEnabled,
            taggingLocationIsEnabled = taggingLocationIsEnabled,
            locationPermissionMustBeRequested = locationPermissionMustBeRequested,
            locationPermissionExplanationIsVisible = locationPermissionExplanationIsVisible,
        )
    }

    fun handleEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.TapBack -> Unit // handled in compose

            SettingsEvent.TapKeepScreenOnToggle -> onKeepScreenOnToggled()

            is SettingsEvent.TapLocationTaggingToggle -> onLocationTaggingToggleTap(
                hasLocationPermission = event.hasLocationPermission,
            )

            SettingsEvent.LocationPermissionRequestLaunched -> {
                locationPermissionMustBeRequested.update { false }
            }

            SettingsEvent.LocationPermissionGranted -> onLocationPermissionGranted()

            SettingsEvent.LocationPermissionDenied -> {
                locationPermissionExplanationIsVisible.update { true }
            }

            SettingsEvent.LocationPermissionDialogDismissed -> {
                locationPermissionExplanationIsVisible.update { false }
            }
        }
    }

    private fun onKeepScreenOnToggled() {
        viewModelScope.launch {
            if (keepScreenOnIsEnabled.firstOrNull() == true) {
                disableKeepScreenOn()
            } else {
                enableKeepScreenOn()
            }
        }
    }

    private suspend fun enableKeepScreenOn() = preferencesRepository.enableKeepScreenOn()

    private suspend fun disableKeepScreenOn() = preferencesRepository.disableKeepScreenOn()

    private fun onLocationTaggingToggleTap(hasLocationPermission: Boolean) {
        viewModelScope.launch {
            when {
                taggingLocationIsEnabled.firstOrNull() == true -> disableTaggingLocation()
                !hasLocationPermission -> requestLocationPermission()
                else -> enableTaggingLocation()
            }
        }
    }

    private fun onLocationPermissionGranted() {
        viewModelScope.launch {
            enableTaggingLocation()
        }
    }

    private fun requestLocationPermission() = locationPermissionMustBeRequested.update { true }

    private suspend fun enableTaggingLocation() = preferencesRepository.enableTaggingLocation()

    private suspend fun disableTaggingLocation() = preferencesRepository.disableTaggingLocation()
}

@Suppress("UNCHECKED_CAST")
class SettingsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(
            preferencesRepository = App.appModule.preferencesRepository,
        ) as T
    }
}
