package com.alexeymerov.unsplashviewer.utils

import java.text.SimpleDateFormat
import java.util.*

fun Date.getHourMinuteString(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(this)