package com.efebudak.photopy.network

import com.efebudak.photopy.BuildConfig
import com.efebudak.photopy.data.PhotoListResponse
import com.efebudak.photopy.data.PhotoSizesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrService {

    @GET("?method=flickr.photos.search")
    suspend fun searchTag(
        @Query("tags") tags: String,
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("format") format: String = "json",
        @Query("nojsoncallback") nojsoncallback: Int = 1
    ): PhotoListResponse

    @GET("?method=flickr.photos.getSizes")
    suspend fun getPhotoSizes(
        @Query("photo_id") photoId: String,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("format") format: String = "json",
        @Query("nojsoncallback") nojsoncallback: Int = 1
    ): PhotoSizesResponse

}