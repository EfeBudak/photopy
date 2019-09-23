package com.efebudak.photopy.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efebudak.photopy.data.PhotoListResponse
import com.efebudak.photopy.data.PhotosPage
import com.efebudak.photopy.data.UiPhoto
import com.efebudak.photopy.data.source.PhotosDataSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import retrofit2.HttpException

private const val PHOTO_INDEX_MARGIN = 10

class SearchViewModel(private val photosDataSource: PhotosDataSource) :
    ViewModel(),
    SearchContract.ViewModel {

    val uiPhotoList: LiveData<MutableList<UiPhoto>>
        get() = _uiPhotoMutableList
    val searchLoading: LiveData<Boolean>
        get() = _searchLoading

    private val _uiPhotoMutableList: MutableLiveData<MutableList<UiPhoto>> = MutableLiveData()
    private val _searchLoading: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * fetching lockers
     */
    private var fetchingSearch = false
    private var fetchingPhotoUrl = false

    /**
     * Search states
     */
    private var biggestRequestedItemIndex = 0
    private var atPage = 1
    private var lastFetchedItemIndex = 0
    private var searchedText = ""
    private var photoUrlJob: Job? = null

    /**
     * Search limits
     */
    private var totalNumberOfPages = 0
    private var currentPhotoNumber = 0
    private var totalNumberOfPhotos = 0

    override fun searchClicked(searchText: String) {

        searchedText = searchText
        if (fetchingSearch) return
        fetchingSearch = true
        photoUrlJob?.cancel()
        _searchLoading.value = true

        initSearchState()

        viewModelScope.launch {

            val photoListResponse = try {
                photosDataSource.fetchPhotoList(searchText, atPage)
            } catch (error: HttpException) {

                //Display error message
                fetchingSearch = false
                _searchLoading.value = false
                cancel()
                PhotoListResponse(PhotosPage())
            }

            yield()

            setLimits(photoListResponse)

            _uiPhotoMutableList.postValue(photoListResponse.photos.photo.map {
                UiPhoto(
                    it.id,
                    it.title
                )
            }.toMutableList())

            fetchingSearch = false
            _searchLoading.value = false
        }
    }

    override fun lastVisibleItemPosition(position: Int) {

        if (biggestRequestedItemIndex < position) {
            biggestRequestedItemIndex = position
        }
        if (fetchingPhotoUrl) return
        fetchingPhotoUrl = true

        if (biggestRequestedItemIndex > lastFetchedItemIndex) {
            photoUrlJob = viewModelScope.launch {

                while (lastFetchedItemIndex < biggestRequestedItemIndex + PHOTO_INDEX_MARGIN) {

                    yield()
                    val photo = uiPhotoList.value?.get(lastFetchedItemIndex) ?: continue

                    val newPhotoUrl = try {
                        val photoSizesResponse = photosDataSource.fetchPhotoSizes(photo.id)
                        photoSizesResponse.photoSizes.photoSizeList
                            .firstOrNull { it.label == "Large Square" }?.sourceUrl ?: ""
                    } catch (error: HttpException) {
                        //Display error message
                        ""
                    }

                    lastFetchedItemIndex++

                    updateListWithNewItem(photo.id, newPhotoUrl)

                    _uiPhotoMutableList.postValue(_uiPhotoMutableList.value)
                }
                fetchingPhotoUrl = false
            }
        } else {
            fetchingPhotoUrl = false
        }
    }

    private fun initSearchState() {
        biggestRequestedItemIndex = 0
        atPage = 1
        lastFetchedItemIndex = 0
    }

    private fun setLimits(photoListResponse: PhotoListResponse) {
        totalNumberOfPages = photoListResponse.photos.pages
        currentPhotoNumber = photoListResponse.photos.photo.size
        totalNumberOfPhotos = try {
            photoListResponse.photos.total.toInt()
        } catch (numberFormatException: NumberFormatException) {
            0
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