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
private const val FIRST_PAGE_INDEX = 1

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
    private var atPage = 0
    private var lastFetchedItemIndex = 0
    private var searchedText = ""
    private var photoUrlJob: Job? = null

    /**
     * Search limits
     */
    private var totalNumberOfPages = 0
    private var currentTotalPhotoNumber = 0
    private var totalNumberOfPhotos = 0

    override fun searchClicked(searchText: String) {

        if (fetchingSearch) return
        fetchingSearch = true
        searchedText = searchText
        photoUrlJob?.cancel()
        fetchingPhotoUrl = false
        _searchLoading.value = true

        initSearchState()

        fetchNextPage()

    }

    override fun lastVisibleItemPosition(position: Int) {

        updateBiggestRequestedItemIndex(position)

        if (reachedTheEnd() && hasMorePagesToLoad()) {
            if (fetchingSearch) return
            fetchingSearch = true
            fetchNextPage()
        }

        if (fetchingPhotoUrl) return
        fetchingPhotoUrl = true

        photoUrlJob = viewModelScope.launch {
            if (biggestRequestedItemIndex > lastFetchedItemIndex) {
                fetchRequestedItemUrls()
            }
            fetchingPhotoUrl = false
        }

    }

    private fun initSearchState() {
        biggestRequestedItemIndex = 0
        atPage = 0
        lastFetchedItemIndex = 0
    }

    private fun updateBiggestRequestedItemIndex(position: Int) {
        if (biggestRequestedItemIndex < position) {
            biggestRequestedItemIndex = position
        }
    }

    private suspend fun fetchRequestedItemUrls() {

        while (hasMoreItemsToLoad()) {

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

    private fun fetchNextPage() {

        atPage++

        viewModelScope.launch {

            val photoListResponse = try {
                photosDataSource.fetchPhotoList(searchedText, atPage)
            } catch (error: HttpException) {

                //Display error message
                fetchingSearch = false
                _searchLoading.value = false
                cancel()
                PhotoListResponse(PhotosPage())
            } catch (exception: Exception) {
                //Display error message
                fetchingSearch = false
                _searchLoading.value = false
                cancel()
                PhotoListResponse(PhotosPage())
            }

            yield()

            // To get the cached page
            atPage = photoListResponse.photos.page
            val fetchedPhotoList = photoListResponse.photos.photo.map {
                UiPhoto(
                    it.id,
                    it.title
                )
            }.toMutableList()
            val newPhotoList = if (atPage > FIRST_PAGE_INDEX) {
                val mergerList = mutableListOf<UiPhoto>()
                mergerList.addAll(_uiPhotoMutableList.value ?: emptyList())
                mergerList.addAll(fetchedPhotoList)
                mergerList
            } else {
                fetchedPhotoList
            }

            setLimits(photoListResponse, newPhotoList.size)

            _uiPhotoMutableList.postValue(newPhotoList.toMutableList())

            updateLastVisibleItemPosition()

            fetchingSearch = false
            _searchLoading.value = false
        }
    }

    private fun setLimits(photoListResponse: PhotoListResponse, currentTotal: Int) {
        totalNumberOfPages = photoListResponse.photos.pages
        currentTotalPhotoNumber = currentTotal
        totalNumberOfPhotos = try {
            photoListResponse.photos.total.toInt()
        } catch (numberFormatException: NumberFormatException) {
            0
        }
    }

    private fun updateLastVisibleItemPosition() {

        if (atPage == FIRST_PAGE_INDEX) {
            lastVisibleItemPosition(1)
        } else {
            lastVisibleItemPosition(biggestRequestedItemIndex)
        }
    }

    private fun hasMoreItemsToLoad() =
        (lastFetchedItemIndex < biggestRequestedItemIndex + PHOTO_INDEX_MARGIN)
                && lastFetchedItemIndex < currentTotalPhotoNumber

    private fun reachedTheEnd() =
        biggestRequestedItemIndex + PHOTO_INDEX_MARGIN >= currentTotalPhotoNumber

    private fun hasMorePagesToLoad() = atPage < totalNumberOfPages

}