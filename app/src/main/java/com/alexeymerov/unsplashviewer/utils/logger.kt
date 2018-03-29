package com.alexeymerov.unsplashviewer.utils

import android.util.Log
import com.alexeymerov.unsplashviewer.BuildConfig

const val TAG = "Merov"

fun infoLog(any: Any, tag: String = TAG) = whenDebug { Log.i(tag, checkNotNull(any.toString())) }

fun errorLog(any: Any, tag: String = TAG) = whenDebug { Log.e(tag, checkNotNull(any.toString())) }

fun errorLog(exception: Exception, tag: String = TAG) = whenDebug { Log.e(tag, checkNotNull(exception.message)) }

fun errorLog(any: Any, tag: String = TAG, tr: Throwable) = whenDebug { Log.e(tag, checkNotNull(any.toString()), tr) }

fun debugLog(any: Any, tag: String = TAG) = whenDebug { Log.d(tag, checkNotNull(any.toString())) }

fun debugLog(any: Any, tag: String = TAG, tr: Throwable) = whenDebug { Log.d(tag, checkNotNull(any.toString()), tr) }

fun verboseLog(any: Any, tag: String = TAG) = whenDebug { Log.v(tag, checkNotNull(any.toString())) }

fun warnLog(any: Any, tag: String = TAG) = whenDebug { Log.w(tag, checkNotNull(any.toString())) }

private inline fun whenDebug(f: () -> Unit) {
    if (BuildConfig.DEBUG) f.invoke()
}

private fun checkNotNull(string: String): String = if (string.isEmpty()) "string for log is null" else string