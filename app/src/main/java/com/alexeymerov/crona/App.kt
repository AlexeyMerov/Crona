package com.alexeymerov.crona

import android.app.Application
import com.alexeymerov.crona.koin.repositoryModule
import com.alexeymerov.crona.koin.viewModelModule
import org.koin.android.ext.android.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
//        Stetho.initializeWithDefaults(this)
        startKoin(this, listOf(viewModelModule, repositoryModule))
    }
}