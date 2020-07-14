package com.alexeymerov.crona.data.server.pojo.response

import com.alexeymerov.crona.data.entity.ImageEntity
import com.squareup.moshi.Json

data class SearchResponse(
    @Json(name = "results")
    var results: List<ImageEntity>
)
