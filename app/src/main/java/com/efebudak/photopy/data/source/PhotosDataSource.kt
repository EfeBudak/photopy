package com.efebudak.photopy.data.source

import com.efebudak.photopy.data.PhotoListResponse

interface PhotosDataSource {

    suspend fun fetchPhotoList(tag:String,atPage: Int = 1): PhotoListResponse
}