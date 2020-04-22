package com.alexeymerov.crona.data.repository.interfaces

import androidx.lifecycle.MutableLiveData
import com.alexeymerov.crona.data.entity.ImageEntity
import com.alexeymerov.crona.data.repository.BaseRepository

abstract class IImageRepository : BaseRepository(), ILoadImageRepository, ISearchImageRepository

interface ILoadImageRepository {

    fun loadImages(): MutableLiveData<LinkedHashSet<ImageEntity>>

    fun loadNext(page: Int)

}

interface ISearchImageRepository {

    fun searchImages(query: String)

    fun searchImagesNext(query: String, page: Int)

}