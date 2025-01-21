package com.example.space_timetagger.di

import android.content.Context
import com.example.space_timetagger.core.data.datastore.preferencesDataStore
import com.example.space_timetagger.core.data.repository.PreferencesRepositoryImpl
import com.example.space_timetagger.core.domain.repository.PreferencesRepository
import com.example.space_timetagger.location.data.repository.LocationRepositoryImpl
import com.example.space_timetagger.location.domain.repository.LocationRepository
import com.example.space_timetagger.sessions.data.repository.SessionsRepositoryImpl
import com.example.space_timetagger.sessions.domain.repository.SessionsRepository

interface AppModule {
    val sessionsRepository: SessionsRepository
    val preferencesRepository: PreferencesRepository
    val locationRepository: LocationRepository
}

class AppModuleImpl(applicationContext: Context) : AppModule {
    private val preferencesDataStore = applicationContext.preferencesDataStore

    override val sessionsRepository by lazy { SessionsRepositoryImpl() }
    override val preferencesRepository by lazy { PreferencesRepositoryImpl(preferencesDataStore) }
    override val locationRepository by lazy { LocationRepositoryImpl(applicationContext) }
}