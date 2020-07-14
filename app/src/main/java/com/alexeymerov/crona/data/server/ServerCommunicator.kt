package com.alexeymerov.crona.data.server

import com.alexeymerov.crona.data.entity.ImageEntity
import io.reactivex.Single

class ServerCommunicator(private val apiService: ApiService) : BaseServerCommunicator() {

    fun getAll(page: Int = 1): Single<LinkedHashSet<ImageEntity>> {
        return apiService.getAll(page).flatMap { Single.just(LinkedHashSet(it)) }.compose(singleTransformer())
    }

    fun searchImages(query: String, page: Int = 1): Single<LinkedHashSet<ImageEntity>> {
        return apiService.search(query, page)
            .map { it.results }
            .flatMap { Single.just(LinkedHashSet(it)) }.compose(singleTransformer())
    }

}