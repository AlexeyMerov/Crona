package com.alexeymerov.crona.domain.interfaces

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alexeymerov.crona.data.entity.ImageEntity
import com.alexeymerov.crona.data.repository.interfaces.IImageRepository
import com.alexeymerov.crona.domain.BaseViewModel

abstract class IImageViewModel(application: Application, repository: IImageRepository) : BaseViewModel(application, repository),
    ISearchImageViewModel, ILoadImageViewModel {

    abstract val notLocalImages: MutableLiveData<LinkedHashSet<ImageEntity>>

}

interface ISearchImageViewModel {

    fun searchImages(query: String)

    fun searchImagesNext(query: String, page: Int)

}

interface ILoadImageViewModel {

    fun loadImages(): MutableLiveData<LinkedHashSet<ImageEntity>>

    fun loadNext(page: Int)

}
