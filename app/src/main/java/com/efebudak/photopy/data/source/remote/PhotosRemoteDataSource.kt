package com.efebudak.photopy.data.source.remote

import com.efebudak.photopy.data.PhotoListResponse
import com.efebudak.photopy.data.source.PhotosDataSource
import com.efebudak.photopy.network.FlickrService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class PhotosRemoteDataSource() : PhotosDataSource, KoinComponent {

    private val flickrService: FlickrService by inject()

    override suspend fun fetchPhotoList(tag: String, atPage: Int): PhotoListResponse =
        withContext(Dispatchers.IO) {
            flickrService.searchTag(tag, atPage)
        }

}