package com.alexeymerov.crona.domain

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.alexeymerov.crona.data.repository.BaseRepository

abstract class BaseViewModel(
    application: Application,
    private vararg val repositories: BaseRepository? = arrayOfNulls(0)
) : AndroidViewModel(application) {

    override fun onCleared() {
        for (repo in repositories) repo?.clean()
        super.onCleared()
    }
}