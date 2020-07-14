package com.alexeymerov.crona.data.repository.image_repository

import androidx.lifecycle.MutableLiveData
import com.alexeymerov.crona.data.entity.ImageEntity
import com.alexeymerov.crona.data.server.ServerCommunicator


class ImageRepository(private val serverCommunicator: ServerCommunicator) : IImageRepository() {

    private val notLocalImages: MutableLiveData<LinkedHashSet<ImageEntity>> by lazy { initLiveData() }

    private fun initLiveData() = MutableLiveData<LinkedHashSet<ImageEntity>>()
        .apply { value = LinkedHashSet() }

    override fun loadImages(): MutableLiveData<LinkedHashSet<ImageEntity>> {
        serverCommunicator.getAll().subscribe({
            notLocalImages.postValue(LinkedHashSet(it))
        }, ::handleError).toComposite()
        return notLocalImages
    }

    override fun loadNext(page: Int) {
        serverCommunicator.getAll(page).subscribe({
            notLocalImages.value?.addAll(it)
            notLocalImages.postValue(notLocalImages.value)
        }, ::handleError).toComposite()
    }

    override fun searchImages(query: String) {
        serverCommunicator.searchImages(query).subscribe({
            notLocalImages.postValue(LinkedHashSet(it))
        }, ::handleError).toComposite()
    }

    override fun searchImagesNext(query: String, page: Int) {
        serverCommunicator.searchImages(query, page).subscribe({
            notLocalImages.value?.addAll(it)
            notLocalImages.postValue(notLocalImages.value)
        }, ::handleError).toComposite()
    }

    private fun handleError(throwable: Throwable) {
        throwable.printStackTrace()
    }
}