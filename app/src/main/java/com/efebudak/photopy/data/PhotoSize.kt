package com.efebudak.photopy.data

import com.squareup.moshi.Json

data class PhotoSize(
    @field:Json(name = "label") val label: String,
    @field:Json(name = "source") val sourceUrl: String
) {
}