package com.alexeymerov.crona.data.repository

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseRepository {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun clean() = compositeDisposable.clear()

    fun Disposable.toComposite() = compositeDisposable.add(this)
}
