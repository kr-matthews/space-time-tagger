package com.example.space_timetagger.di

import android.content.Context
import androidx.room.Room
import com.example.space_timetagger.core.data.datastore.preferencesDataStore
import com.example.space_timetagger.core.data.repository.PreferencesRepositoryImpl
import com.example.space_timetagger.core.domain.repository.PreferencesRepository
import com.example.space_timetagger.database.data.MIGRATION_1_2
import com.example.space_timetagger.database.data.RoomSessionsDataSource
import com.example.space_timetagger.database.data.SessionsDatabase
import com.example.space_timetagger.location.data.repository.LocationRepositoryImpl
import com.example.space_timetagger.location.domain.hasFineLocationPermission
import com.example.space_timetagger.location.domain.repository.LocationRepository
import com.example.space_timetagger.sessions.data.repository.SessionsRepositoryImpl
import com.example.space_timetagger.sessions.domain.repository.SessionsRepository
import kotlinx.coroutines.Dispatchers

interface AppModule {
    val sessionsRepository: SessionsRepository
    val preferencesRepository: PreferencesRepository
    val locationRepository: LocationRepository
}

class AppModuleImpl(applicationContext: Context) : AppModule {
    private val preferencesDataStore = applicationContext.preferencesDataStore
    private val hasLocationPermission = applicationContext.hasFineLocationPermission()
    private val sessionsDatabase =
        Room.databaseBuilder(applicationContext, SessionsDatabase::class.java, "sessions.db")
            .addMigrations(MIGRATION_1_2)
            .build()
    private val ioDispatcher = Dispatchers.IO
    private val sessionsDataSource =
        RoomSessionsDataSource(sessionsDatabase.sessionsDao, ioDispatcher)

    override val sessionsRepository by lazy { SessionsRepositoryImpl(sessionsDataSource) }
    override val preferencesRepository by lazy {
        PreferencesRepositoryImpl(preferencesDataStore, hasLocationPermission)
    }
    override val locationRepository by lazy { LocationRepositoryImpl(applicationContext) }
}