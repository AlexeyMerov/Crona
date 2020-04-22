package com.alexeymerov.crona.koin

import com.alexeymerov.crona.domain.ImageViewModel
import com.alexeymerov.crona.domain.interfaces.IImageViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

val viewModelModule: Module = module {
    viewModel { ImageViewModel(get(), get()) as IImageViewModel }
}
