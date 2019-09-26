package com.efebudak.photopy.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.efebudak.photopy.data.source.PhotosDataSource

class SearchViewModelFactory(
    private val photosDataSource: PhotosDataSource,
    private val stateHolder: SearchContract.StateHolder
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchViewModel(photosDataSource, stateHolder) as T
    }
}