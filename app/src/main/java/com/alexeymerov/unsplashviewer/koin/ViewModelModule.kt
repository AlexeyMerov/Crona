package com.alexeymerov.unsplashviewer.koin

import com.alexeymerov.unsplashviewer.domain.ImageViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

val viewModelModule: Module = applicationContext {
    viewModel { ImageViewModel(get(), get()) }
}
