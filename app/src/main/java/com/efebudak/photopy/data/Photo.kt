package com.efebudak.photopy.data

import com.squareup.moshi.Json

data class Photo(
    @field:Json(name = "id") val id: String = "",
    @field:Json(name = "title") val title: String = ""
) {
}