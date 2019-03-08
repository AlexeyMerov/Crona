package com.alexeymerov.unsplashviewer.data.server

import io.reactivex.SingleTransformer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

abstract class BaseServerCommunicator {

    companion object {
        private const val DEFAULT_TIMEOUT = 10 // seconds
        private const val DEFAULT_RETRY_ATTEMPTS: Long = 4
    }

    protected fun <T> singleTransformer(): SingleTransformer<T, T> = SingleTransformer {
        it.subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .timeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .retry(DEFAULT_RETRY_ATTEMPTS)
    }
}