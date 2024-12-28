package com.example.space_timetagger.core.data.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

private const val PREFERENCES_DATA_STORE_NAME = "PREFERENCES_DATA_STORE_NAME"

val Context.preferencesDataStore by preferencesDataStore(PREFERENCES_DATA_STORE_NAME)
