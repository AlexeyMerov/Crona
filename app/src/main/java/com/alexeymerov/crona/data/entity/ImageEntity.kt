package com.alexeymerov.crona.data.entity

import com.squareup.moshi.Json

data class ImageEntity(

    @Json(name = "id")
    var id: String,

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

)

data class Urls(

    @Json(name = "thumb")
    var thumb: String,

    @Json(name = "raw")
    var raw: String,

    @Json(name = "regular")
    var regular: String

)