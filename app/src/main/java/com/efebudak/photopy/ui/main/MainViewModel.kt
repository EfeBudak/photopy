package com.efebudak.photopy.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efebudak.photopy.data.UiPhoto
import com.efebudak.photopy.data.source.PhotosDataSource
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class MainViewModel(
    private val photosDataSource: PhotosDataSource
) : ViewModel(), MainContract.Presenter {

    val uiPhotoMutableList: MutableLiveData<List<UiPhoto>> = MutableLiveData()

    private var atPage = 1


    @ExperimentalTime
    override fun searchClicked(searchText: String) {

        viewModelScope.launch {

            val a = measureTime {
                val photoListResponse = photosDataSource.fetchPhotoList(searchText, atPage)

                uiPhotoMutableList.value =
                    photoListResponse.photos.photo.map { UiPhoto(it.id, it.title) }
                for (photo in photoListResponse.photos.photo) {

                    val photoSizesResponse = photosDataSource.fetchPhotoSizes(photo.id)

                    uiPhotoMutableList.value?.firstOrNull { it.id == photo.id }?.photoUrl =
                        photoSizesResponse.photoSizes.photoSizeList
                            .firstOrNull { it.label == "Large Square" }?.sourceUrl ?: ""

                }
            }

            Log.d("Total time", "${a.inSeconds}")

        }
    }

}