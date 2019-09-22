package com.efebudak.photopy.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efebudak.photopy.data.UiPhoto
import com.efebudak.photopy.data.source.PhotosDataSource
import kotlinx.coroutines.launch

class SearchViewModel(
    private val photosDataSource: PhotosDataSource
) : ViewModel(), SearchContract.ViewModel {

    private val _uiPhotoMutableList: MutableLiveData<MutableList<UiPhoto>> = MutableLiveData()

    val uiPhotoList: LiveData<MutableList<UiPhoto>>
        get() {
            return _uiPhotoMutableList
        }

    private var fetchingSearch = false
    private var fetchingPhotoUrl = false

    private var biggestRequestedItemIndex = 0
    private var atPage = 1
    private var lastVisibleItem = 0

    override fun searchClicked(searchText: String) {

        if (fetchingSearch) return
        fetchingSearch = true
        lastVisibleItem = 0
        viewModelScope.launch {

            val photoListResponse = photosDataSource.fetchPhotoList(searchText, atPage)

            _uiPhotoMutableList.postValue(photoListResponse.photos.photo.map {
                UiPhoto(
                    it.id,
                    it.title
                )
            }.toMutableList())

            fetchingSearch = false
        }
    }

    override fun lastVisibleItemPosition(position: Int) {

        val photoList = uiPhotoList.value?.let { it } ?: return
        if (biggestRequestedItemIndex < position) {
            biggestRequestedItemIndex = position
        }
        if (fetchingPhotoUrl) return
        fetchingPhotoUrl = true

//todo add pagination check list size
        if (biggestRequestedItemIndex > lastVisibleItem) {
            viewModelScope.launch {

                for (index in lastVisibleItem..biggestRequestedItemIndex + 6) {

                    val photo = uiPhotoList.value?.get(index) ?: continue

                    val photoSizesResponse = photosDataSource.fetchPhotoSizes(photo.id)

                    lastVisibleItem = index
                    val newPhotoUrl = photoSizesResponse.photoSizes.photoSizeList
                        .firstOrNull { it.label == "Large Square" }?.sourceUrl ?: ""

                    updateListWithNewItem(photo.id, newPhotoUrl)

                    _uiPhotoMutableList.postValue(_uiPhotoMutableList.value)
                }
                fetchingPhotoUrl = false
            }
        } else {
            fetchingPhotoUrl = false
        }
    }

    private fun updateListWithNewItem(photoId: String, newPhotoUrl: String) {
        val oldUiPhoto = _uiPhotoMutableList.value?.firstOrNull { it.id == photoId }
        val itemIndex = _uiPhotoMutableList.value?.indexOf(oldUiPhoto)

        if (itemIndex != null) {
            oldUiPhoto?.copy(photoUrl = newPhotoUrl)?.run {
                _uiPhotoMutableList.value?.removeAt(itemIndex)
                _uiPhotoMutableList.value?.add(itemIndex, this)
            }
        }
    }

}