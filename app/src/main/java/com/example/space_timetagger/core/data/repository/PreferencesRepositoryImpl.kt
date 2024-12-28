package com.example.space_timetagger.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.space_timetagger.core.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val TAGGING_LOCATION = booleanPreferencesKey("tagging_location")

class PreferencesRepositoryImpl(
    private val preferencesDataStore: DataStore<Preferences>,
) : PreferencesRepository {
    override val taggingLocationIsEnabled: Flow<Boolean> =
        preferencesDataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[TAGGING_LOCATION] ?: false
        }

    override suspend fun enableTaggingLocation() = toggleTaggingLocation(true)

    override suspend fun disableTaggingLocation() = toggleTaggingLocation(false)

    private suspend fun toggleTaggingLocation(isEnabled: Boolean) {
        preferencesDataStore.edit { preferences ->
            preferences[TAGGING_LOCATION] = isEnabled
        }
    }
}
