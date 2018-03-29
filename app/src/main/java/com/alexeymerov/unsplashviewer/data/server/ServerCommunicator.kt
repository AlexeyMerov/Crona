package com.alexeymerov.unsplashviewer.data.server

import com.alexeymerov.unsplashviewer.data.database.entity.ImageEntity
import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ServerCommunicator(private val apiService: ApiService) {

    companion object {
        private const val DEFAULT_TIMEOUT = 10 // seconds
        private const val DEFAULT_RETRY_ATTEMPTS: Long = 4
    }

//    private fun <T> observableTransformer(): ObservableTransformer<T, T> = ObservableTransformer {
//        it.subscribeOn(Schedulers.io())
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .timeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
//                .retry(DEFAULT_RETRY_ATTEMPTS)
//    }


    private fun <T> singleTransformer(): SingleTransformer<T, T> = SingleTransformer {
        it.subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .timeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .retry(DEFAULT_RETRY_ATTEMPTS)
    }

//    private fun completableTransformer(): CompletableTransformer = CompletableTransformer {
//        it.subscribeOn(Schedulers.io())
////                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .timeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
//                .retry(DEFAULT_RETRY_ATTEMPTS)
//    }

    fun getAll(page: Int = 1): Single<LinkedHashSet<ImageEntity>> {
        return apiService.getAll(page).flatMap { Single.just(LinkedHashSet(it)) }.compose(singleTransformer())
    }

    fun searchImages(query: String, page: Int = 1): Single<LinkedHashSet<ImageEntity>> {
        return apiService.search(query, page)
                .map { it.results }
                .flatMap { Single.just(LinkedHashSet(it)) }.compose(singleTransformer())
    }

}