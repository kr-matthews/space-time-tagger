package com.example.space_timetagger.di

import com.example.space_timetagger.data.repository.SessionsRepositoryImpl
import com.example.space_timetagger.domain.repository.SessionsRepository

interface AppModule {
    val sessionsRepository: SessionsRepository
}

class AppModuleImpl : AppModule {
    override val sessionsRepository by lazy { SessionsRepositoryImpl() }
}