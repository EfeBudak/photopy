package com.efebudak.photopy.data

import com.squareup.moshi.Json

data class PhotoSizes(
    @field:Json(name = "size") val photoSizeList: List<PhotoSize>
) {
}