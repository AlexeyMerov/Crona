package com.alexeymerov.unsplashviewer.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.alexeymerov.unsplashviewer.data.database.dao.ImageDAO
import com.alexeymerov.unsplashviewer.data.database.entity.ImageEntity

@Database(entities = [ImageEntity::class], version = 1, exportSchema = false)
abstract class ApplicationDatabase : RoomDatabase() {

    abstract fun imageDao(): ImageDAO

}