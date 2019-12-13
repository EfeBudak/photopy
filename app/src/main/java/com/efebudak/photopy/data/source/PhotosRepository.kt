package com.efebudak.photopy.data.source

import androidx.collection.ArrayMap
import com.efebudak.photopy.data.Photo
import com.efebudak.photopy.data.PhotoListResponse
import com.efebudak.photopy.data.PhotoSizesResponse
import com.efebudak.photopy.data.PhotosPage

class PhotosRepository(private val remoteDataSource: PhotosDataSource) : PhotosDataSource {

  private var photoListResponseCache = ArrayMap<String, PhotoListResponse>()
  private var photoSizesCache = ArrayMap<String, PhotoSizesResponse>()

  override suspend fun fetchPhotoList(tag: String, atPage: Int): PhotoListResponse {

    val cachedPhotoListResponse = photoListResponseCache[tag]

    cachedPhotoListResponse?.let {
      if (it.photos.page >= atPage) {
        return cachedPhotoListResponse
      } else if (it.photos.page < atPage - 1) {
        throw Exception("Pages MUST be incremented by 1")
      }
    }

    val remotePhotoListResponse = remoteDataSource.fetchPhotoList(tag, atPage)

    photoListResponseCache[tag] = if (cachedPhotoListResponse != null) {
      val mergePhotoList = mutableListOf<Photo>()
      mergePhotoList.addAll(cachedPhotoListResponse.photos.photo)
      mergePhotoList.addAll(remotePhotoListResponse.photos.photo)

      val mergePhotoListResponse = PhotoListResponse(
        PhotosPage(
          remotePhotoListResponse.photos.page,
          remotePhotoListResponse.photos.pages,
          remotePhotoListResponse.photos.perpage,
          remotePhotoListResponse.photos.total,
          mergePhotoList
        )
      )
      mergePhotoListResponse
    } else {
      remotePhotoListResponse
    }

    return remotePhotoListResponse
  }

  override suspend fun fetchPhotoSizes(photoId: String): PhotoSizesResponse {

    val cachedPhotoSizesResponse = photoSizesCache[photoId]

    return if (cachedPhotoSizesResponse == null) {
      val remotePhotoSizesResponse = remoteDataSource.fetchPhotoSizes(photoId)
      photoSizesCache[photoId] = remotePhotoSizesResponse

      remotePhotoSizesResponse
    } else {
      cachedPhotoSizesResponse
    }

  }
}