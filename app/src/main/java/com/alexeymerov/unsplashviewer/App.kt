package com.alexeymerov.unsplashviewer

import android.app.Application
import com.alexeymerov.unsplashviewer.koin.repositoryModule
import com.alexeymerov.unsplashviewer.koin.viewModelModule
import org.koin.android.ext.android.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
//        Stetho.initializeWithDefaults(this)
        startKoin(this, listOf(viewModelModule, repositoryModule))
    }
}