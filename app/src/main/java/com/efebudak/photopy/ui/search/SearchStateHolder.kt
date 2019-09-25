package com.efebudak.photopy.ui.search

import kotlinx.coroutines.Job

class SearchStateHolder : SearchContract.StateHolder {

    override var fetchingSearch: Boolean = false

    override var fetchingPhotoUrl: Boolean = false

    override var biggestRequestedItemIndex: Int = 0

    override var atPage: Int = 0

    override var lastFetchedItemIndex: Int = 0

    override var searchedText: String = ""

    override var photoUrlJob: Job? = null

    override var totalNumberOfPages: Int = 0

    override var currentTotalPhotoNumber: Int = 0

    override var totalNumberOfPhotos: Int = 0
}