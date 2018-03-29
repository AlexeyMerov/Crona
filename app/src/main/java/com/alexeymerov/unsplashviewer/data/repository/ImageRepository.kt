package com.alexeymerov.unsplashviewer.data.repository

import android.arch.lifecycle.MutableLiveData
import com.alexeymerov.unsplashviewer.data.database.ApplicationDatabase
import com.alexeymerov.unsplashviewer.data.database.entity.ImageEntity
import com.alexeymerov.unsplashviewer.data.server.ServerCommunicator


class ImageRepository(private val serverCommunicator: ServerCommunicator,
                      private val database: ApplicationDatabase) {

    private val notLocalImages: MutableLiveData<LinkedHashSet<ImageEntity>> by lazy { initLiveData() }

    private fun initLiveData() = MutableLiveData<LinkedHashSet<ImageEntity>>()
            .apply { value = LinkedHashSet() }

    fun loadImages(): MutableLiveData<LinkedHashSet<ImageEntity>> {
        serverCommunicator.getAll().subscribe({
            notLocalImages.postValue(LinkedHashSet(it))
        }, Throwable::printStackTrace)
        return notLocalImages
    }

    fun loadNext(page: Int) {
        serverCommunicator.getAll(page).subscribe({
            notLocalImages.value?.addAll(it)
            notLocalImages.postValue(notLocalImages.value)
        }, Throwable::printStackTrace)
    }

    fun searchImages(query: String) {
        serverCommunicator.searchImages(query).subscribe({
            notLocalImages.postValue(LinkedHashSet(it))
        }, Throwable::printStackTrace)
    }

    fun searchImagesNext(query: String, page: Int) {
        serverCommunicator.searchImages(query, page).subscribe({
            notLocalImages.value?.addAll(it)
            notLocalImages.postValue(notLocalImages.value)
        }, Throwable::printStackTrace)
    }

}