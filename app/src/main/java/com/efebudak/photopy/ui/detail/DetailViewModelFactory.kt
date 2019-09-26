package com.efebudak.photopy.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.efebudak.photopy.data.source.PhotosDataSource

class DetailViewModelFactory(private val photosDataSource: PhotosDataSource) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DetailViewModel(photosDataSource) as T
    }
}
