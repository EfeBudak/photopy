package com.efebudak.photopy.data

import com.squareup.moshi.Json

data class PhotoSizesResponse(
    @field:Json(name = "sizes") val photoSizes: PhotoSizes,
    @field:Json(name = "stat") val stat: String = "ok"
) {
}