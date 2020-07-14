package com.alexeymerov.crona.domain

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alexeymerov.crona.data.entity.ImageEntity
import com.alexeymerov.crona.data.repository.image_repository.IImageRepository
import com.alexeymerov.crona.domain.interfaces.IImageViewModel
import java.util.*

class ImageViewModel(application: Application, private val repository: IImageRepository) : IImageViewModel(application, repository) {

    override val notLocalImages: MutableLiveData<LinkedHashSet<ImageEntity>> by lazy { loadImages() }

    override fun loadImages() = repository.loadImages()

    override fun loadNext(page: Int) = repository.loadNext(page)

    override fun searchImages(query: String) = repository.searchImages(query)

    override fun searchImagesNext(query: String, page: Int) = repository.searchImagesNext(query, page)

}