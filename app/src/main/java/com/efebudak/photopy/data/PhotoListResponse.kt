package com.efebudak.photopy.data

import com.squareup.moshi.Json

data class PhotoListResponse(
    @field:Json(name = "photos") val photos: PhotosPage,
    @field:Json(name = "stat") val stat: String = "ok"
) {
}