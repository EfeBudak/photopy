package com.efebudak.photopy.data.source

import com.efebudak.photopy.data.PhotoListResponse
import com.efebudak.photopy.data.PhotoSizesResponse

interface PhotosDataSource {

  suspend fun fetchPhotoList(tag: String, atPage: Int = 1): PhotoListResponse
  suspend fun fetchPhotoSizes(photoId: String): PhotoSizesResponse
}