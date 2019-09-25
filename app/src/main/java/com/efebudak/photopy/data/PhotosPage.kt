package com.efebudak.photopy.data

import com.squareup.moshi.Json

data class PhotosPage(
    @field:Json(name = "page") val page: Int = 1,
    @field:Json(name = "pages") val pages: Int = 1,
    @field:Json(name = "perpage") val perpage: Int = 100,
    @field:Json(name = "total") val total: String = "0",
    @field:Json(name = "photo") val photo: List<Photo> = emptyList()
) {
}