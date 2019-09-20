package com.efebudak.photopy.data.source

import com.efebudak.photopy.data.PhotoListResponse
import com.efebudak.photopy.data.PhotoSizesResponse
import org.koin.core.KoinComponent

class PhotosRepository(
    private val localDataSource: PhotosDataSource,
    private val remoteDataSource: PhotosDataSource
) : PhotosDataSource, KoinComponent {

    override suspend fun fetchPhotoList(tag: String, atPage: Int): PhotoListResponse {

        return remoteDataSource.fetchPhotoList(tag, atPage)
    }

    override suspend fun fetchPhotoSizes(photoId: String): PhotoSizesResponse {
        return remoteDataSource.fetchPhotoSizes(photoId)
    }
}