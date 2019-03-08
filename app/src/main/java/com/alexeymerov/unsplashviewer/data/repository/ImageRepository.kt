package com.alexeymerov.unsplashviewer.data.repository

import androidx.lifecycle.MutableLiveData
import com.alexeymerov.unsplashviewer.data.entity.ImageEntity
import com.alexeymerov.unsplashviewer.data.server.ServerCommunicator


class ImageRepository(private val serverCommunicator: ServerCommunicator) : BaseRepository() {

    private val notLocalImages: MutableLiveData<LinkedHashSet<ImageEntity>> by lazy { initLiveData() }

    private fun initLiveData() = MutableLiveData<LinkedHashSet<ImageEntity>>()
            .apply { value = LinkedHashSet() }

    fun loadImages(): MutableLiveData<LinkedHashSet<ImageEntity>> {
        serverCommunicator.getAll().subscribe({
            notLocalImages.postValue(LinkedHashSet(it))
        }, ::handleError).toComposite()
        return notLocalImages
    }

    fun loadNext(page: Int) {
        serverCommunicator.getAll(page).subscribe({
            notLocalImages.value?.addAll(it)
            notLocalImages.postValue(notLocalImages.value)
        }, ::handleError).toComposite()
    }

    fun searchImages(query: String) {
        serverCommunicator.searchImages(query).subscribe({
            notLocalImages.postValue(LinkedHashSet(it))
        }, ::handleError).toComposite()
    }

    fun searchImagesNext(query: String, page: Int) {
        serverCommunicator.searchImages(query, page).subscribe({
            notLocalImages.value?.addAll(it)
            notLocalImages.postValue(notLocalImages.value)
        }, ::handleError).toComposite()
    }

    private fun handleError(throwable: Throwable) {
        throwable.printStackTrace()
    }
}