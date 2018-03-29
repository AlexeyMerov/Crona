package com.alexeymerov.unsplashviewer.data.server.pojo.response

import com.alexeymerov.unsplashviewer.data.database.entity.ImageEntity
import com.squareup.moshi.Json

data class SearchResponse(
        @Json(name = "results")
        var results: List<ImageEntity>
)
