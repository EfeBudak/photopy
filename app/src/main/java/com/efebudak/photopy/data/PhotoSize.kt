package com.efebudak.photopy.data

import com.squareup.moshi.Json

data class PhotoSize(
    @field:Json(name = "label") val label: String = "",
    @field:Json(name = "source") val sourceUrl: String = "",
    @field:Json(name = "width") val width: Int = 0,
    @field:Json(name = "height") val height: Int = 0
)