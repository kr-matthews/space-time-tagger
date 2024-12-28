package com.example.space_timetagger

import android.app.Application
import com.example.space_timetagger.di.AppModule
import com.example.space_timetagger.di.AppModuleImpl

class App : Application() {
    companion object {
        lateinit var appModule: AppModule
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(applicationContext)
    }
}