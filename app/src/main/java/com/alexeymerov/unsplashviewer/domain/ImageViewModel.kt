package com.alexeymerov.unsplashviewer.domain

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.alexeymerov.unsplashviewer.data.database.entity.ImageEntity
import com.alexeymerov.unsplashviewer.data.repository.ImageRepository
import java.util.*

class ImageViewModel(application: Application, private val repository: ImageRepository) : BaseViewModel(application) {

    val notLocalImages: MutableLiveData<LinkedHashSet<ImageEntity>> by lazy { loadImages() }

    fun loadImages() = repository.loadImages()

    fun loadNext(page: Int) = repository.loadNext(page)

    fun searchImages(query: String) = repository.searchImages(query)

    fun searchImagesNext(query: String, page: Int) = repository.searchImagesNext(query, page)

}