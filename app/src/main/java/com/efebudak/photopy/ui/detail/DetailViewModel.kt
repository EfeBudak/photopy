package com.efebudak.photopy.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efebudak.photopy.data.PhotoSize
import com.efebudak.photopy.data.PhotoSizes
import com.efebudak.photopy.data.PhotoSizesResponse
import com.efebudak.photopy.data.source.PhotosDataSource
import kotlinx.coroutines.launch

class DetailViewModel(private val photosDataSource: PhotosDataSource) :
  ViewModel(),
  DetailContract.ViewModel {

  override val largePhotoUrl: LiveData<PhotoSize>
    get() = _largePhotoUrl

  private val _largePhotoUrl: MutableLiveData<PhotoSize> = MutableLiveData()

  override fun created(photoId: String) {

    viewModelScope.launch {

      val photoSizesResponse = try {
        photosDataSource.fetchPhotoSizes(photoId)
      } catch (exception: Exception) {
        //Send proper error message
        PhotoSizesResponse(PhotoSizes(emptyList()))
      }

      val photoUrl = photoSizesResponse.photoSizes.photoSizeList
        .firstOrNull { it.label == "Large" || it.label == "Original" } ?: PhotoSize()

      _largePhotoUrl.postValue(photoUrl)
    }

  }

}