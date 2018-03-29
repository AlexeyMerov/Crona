package com.alexeymerov.unsplashviewer.data.database.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import com.alexeymerov.unsplashviewer.data.database.dao.ImageDAO
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Entity(tableName = ImageDAO.TABLE_NAME)
@Parcelize
data class ImageEntity(

        @PrimaryKey
        @Json(name = "id")
        var id: String,

        @Embedded
        @Json(name = "urls")
        var urls: Urls,

        @Json(name = "color")
        var color: String? = null,

        @Json(name = "created_at")
        var createdAt: String,

        @Json(name = "width")
        var width: Int,

        @Json(name = "height")
        var height: Int
) : Parcelable

@Entity(tableName = "image_urls")
@Parcelize
data class Urls(

        @Json(name = "thumb")
        var thumb: String,

        @Json(name = "raw")
        var raw: String,

        @Json(name = "regular")
        var regular: String
) : Parcelable