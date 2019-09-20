package com.efebudak.photopy.data.source.remote

import com.efebudak.photopy.data.PhotoListResponse
import com.efebudak.photopy.data.PhotoSizesResponse
import com.efebudak.photopy.data.source.PhotosDataSource
import com.efebudak.photopy.network.FlickrService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhotosRemoteDataSource(private val flickrService: FlickrService) : PhotosDataSource {

    override suspend fun fetchPhotoList(tag: String, atPage: Int): PhotoListResponse =
        withContext(Dispatchers.IO) {
            flickrService.searchTag(tag, atPage)
        }

    override suspend fun fetchPhotoSizes(photoId: String): PhotoSizesResponse =
        withContext(Dispatchers.IO) {
            flickrService.getPhotoSizes(photoId)
        }

}