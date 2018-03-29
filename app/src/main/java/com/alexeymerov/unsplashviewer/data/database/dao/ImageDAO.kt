package com.alexeymerov.unsplashviewer.data.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.alexeymerov.unsplashviewer.data.database.entity.ImageEntity

@Dao
abstract class ImageDAO {

    companion object {
        const val TABLE_NAME: String = "image_entity"
    }

    @Query("SELECT * FROM $TABLE_NAME")
    abstract fun getAll(): LiveData<List<ImageEntity>>

    @Query("SELECT * FROM $TABLE_NAME")
    abstract fun getAllNotLive(): List<ImageEntity>

    @Insert(onConflict = REPLACE)
    abstract fun addAll(all: LinkedHashSet<ImageEntity>)

    @Update(onConflict = REPLACE)
    abstract fun updateAll(all: List<ImageEntity>)

    @Delete
    abstract fun deleteAll(all: List<ImageEntity>)

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    abstract fun getByIdLive(id: Long): LiveData<ImageEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    abstract fun getById(id: Long): ImageEntity

    @Update(onConflict = REPLACE)
    abstract fun update(ImageEntity: ImageEntity)

    @Insert(onConflict = REPLACE)
    abstract fun add(ImageEntity: ImageEntity)

}