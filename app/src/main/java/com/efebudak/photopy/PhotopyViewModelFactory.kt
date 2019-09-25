package com.efebudak.photopy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.efebudak.photopy.data.source.PhotosDataSource
import com.efebudak.photopy.ui.search.SearchContract
import com.efebudak.photopy.ui.search.SearchViewModel

class PhotopyViewModelFactory(
    private val photosDataSource: PhotosDataSource,
    private val stateHolder: SearchContract.StateHolder
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchViewModel(photosDataSource, stateHolder) as T
    }
}