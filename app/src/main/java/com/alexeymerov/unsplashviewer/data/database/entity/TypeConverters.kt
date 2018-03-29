package com.alexeymerov.unsplashviewer.data.database.entity

import android.arch.persistence.room.TypeConverter
import java.util.*


class DateConverter {
    @TypeConverter
    fun fromDatabase(value: Long): Date = Date(value)

    @TypeConverter
    fun toDatabase(date: Date): Long = date.time
}
