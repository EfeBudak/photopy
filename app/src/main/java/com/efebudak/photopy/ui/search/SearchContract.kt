package com.efebudak.photopy.ui.search

import androidx.lifecycle.LiveData
import com.efebudak.photopy.data.UiPhoto
import kotlinx.coroutines.Job

interface SearchContract {

    interface StateHolder {
        /**
         * fetching lockers
         */
        var fetchingSearch: Boolean
        var fetchingPhotoUrl: Boolean

        /**
         * Search states
         */
        var biggestRequestedItemIndex: Int
        var atPage: Int
        var lastFetchedItemIndex: Int
        var searchedText: String
        var photoUrlJob: Job?

        /**
         * Search limits
         */
        var totalNumberOfPages: Int
        var currentTotalPhotoNumber: Int
        var totalNumberOfPhotos: Int
    }

    interface ViewModel {
        fun searchClicked(searchText: String)
        fun lastVisibleItemPosition(position: Int)
        val uiPhotoList: LiveData<MutableList<UiPhoto>>
        val searchLoading: LiveData<Boolean>
    }

}