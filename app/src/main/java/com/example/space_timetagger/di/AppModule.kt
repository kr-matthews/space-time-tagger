package com.example.space_timetagger.di

import com.example.space_timetagger.sessions.data.repository.SessionsRepositoryImpl
import com.example.space_timetagger.sessions.domain.repository.SessionsRepository

interface AppModule {
    val sessionsRepository: SessionsRepository
}

class AppModuleImpl : AppModule {
    override val sessionsRepository by lazy { SessionsRepositoryImpl() }
}