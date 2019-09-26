package com.efebudak.photopy.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efebudak.photopy.data.PhotoListResponse
import com.efebudak.photopy.data.PhotosPage
import com.efebudak.photopy.data.UiPhoto
import com.efebudak.photopy.data.source.PhotosDataSource
import com.efebudak.photopy.utils.BaseCoroutineContextProvider
import com.efebudak.photopy.utils.CoroutineContextProvider
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import retrofit2.HttpException

private const val PHOTO_INDEX_MARGIN = 10
private const val FIRST_PAGE_INDEX = 1

class SearchViewModel(
    private val photosDataSource: PhotosDataSource,
    private var stateHolder: SearchContract.StateHolder,
    private var coroutineContextProvider: BaseCoroutineContextProvider = CoroutineContextProvider()
) : ViewModel(),
    SearchContract.ViewModel {

    override val uiPhotoList: LiveData<MutableList<UiPhoto>>
        get() = _uiPhotoMutableList
    override val searchLoading: LiveData<Boolean>
        get() = _searchLoading

    private val _uiPhotoMutableList: MutableLiveData<MutableList<UiPhoto>> = MutableLiveData()
    private val _searchLoading: MutableLiveData<Boolean> = MutableLiveData()

    override fun searchClicked(searchText: String) {

        if (stateHolder.fetchingSearch) return
        stateHolder.fetchingSearch = true
        stateHolder.searchedText = searchText
        stateHolder.photoUrlJob?.cancel()
        stateHolder.fetchingPhotoUrl = false
        _searchLoading.value = true

        initSearchState()

        fetchNextPage()

    }

    override fun lastVisibleItemPosition(position: Int) {

        updateBiggestRequestedItemIndex(position)

        if (reachedTheEnd() && hasMorePagesToLoad()) {
            if (stateHolder.fetchingSearch) return
            stateHolder.fetchingSearch = true
            fetchNextPage()
        }

        if (stateHolder.fetchingPhotoUrl) return
        stateHolder.fetchingPhotoUrl = true

        stateHolder.photoUrlJob = viewModelScope.launch {
            if (stateHolder.biggestRequestedItemIndex > stateHolder.lastFetchedItemIndex) {
                fetchRequestedItemUrls()
            }
            stateHolder.fetchingPhotoUrl = false
        }

    }

    private fun initSearchState() {
        stateHolder.biggestRequestedItemIndex = 0
        stateHolder.atPage = 0
        stateHolder.lastFetchedItemIndex = 0
    }

    private fun updateBiggestRequestedItemIndex(position: Int) {
        if (stateHolder.biggestRequestedItemIndex < position) {
            stateHolder.biggestRequestedItemIndex = position
        }
    }

    private suspend fun fetchRequestedItemUrls() {

        while (hasMoreItemsToLoad()) {

            yield()
            val photo = uiPhotoList.value?.get(stateHolder.lastFetchedItemIndex) ?: continue

            val newPhotoUrl = try {
                val photoSizesResponse = photosDataSource.fetchPhotoSizes(photo.id)
                photoSizesResponse.photoSizes.photoSizeList
                    .firstOrNull { it.label == "Large Square" }?.sourceUrl ?: ""
            } catch (error: HttpException) {
                //Display error message
                ""
            }

            stateHolder.lastFetchedItemIndex++

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

        stateHolder.atPage++

        viewModelScope.launch(coroutineContextProvider.main) {

            val photoListResponse = try {
                photosDataSource.fetchPhotoList(stateHolder.searchedText, stateHolder.atPage)
            } catch (error: HttpException) {

                //Display error message
                stateHolder.fetchingSearch = false
                _searchLoading.value = false
                cancel()
                PhotoListResponse(PhotosPage())
            } catch (exception: Exception) {
                //Display error message
                stateHolder.fetchingSearch = false
                _searchLoading.value = false
                cancel()
                PhotoListResponse(PhotosPage())
            }

            yield()

            // To get the cached page
            stateHolder.atPage = photoListResponse.photos.page
            val fetchedPhotoList = photoListResponse.photos.photo.map {
                UiPhoto(
                    it.id,
                    it.title
                )
            }.toMutableList()
            val newPhotoList = if (stateHolder.atPage > FIRST_PAGE_INDEX) {
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

            stateHolder.fetchingSearch = false
            _searchLoading.value = false
        }
    }

    private fun setLimits(photoListResponse: PhotoListResponse, currentTotal: Int) {
        stateHolder.totalNumberOfPages = photoListResponse.photos.pages
        stateHolder.currentTotalPhotoNumber = currentTotal
        stateHolder.totalNumberOfPhotos = try {
            photoListResponse.photos.total.toInt()
        } catch (numberFormatException: NumberFormatException) {
            0
        }
    }

    private fun updateLastVisibleItemPosition() {

        if (stateHolder.atPage == FIRST_PAGE_INDEX) {
            lastVisibleItemPosition(1)
        } else {
            lastVisibleItemPosition(stateHolder.biggestRequestedItemIndex)
        }
    }

    private fun hasMoreItemsToLoad() =
        (stateHolder.lastFetchedItemIndex < stateHolder.biggestRequestedItemIndex + PHOTO_INDEX_MARGIN)
                && stateHolder.lastFetchedItemIndex < stateHolder.currentTotalPhotoNumber

    private fun reachedTheEnd() =
        stateHolder.biggestRequestedItemIndex + PHOTO_INDEX_MARGIN >= stateHolder.currentTotalPhotoNumber

    private fun hasMorePagesToLoad() = stateHolder.atPage < stateHolder.totalNumberOfPages

}