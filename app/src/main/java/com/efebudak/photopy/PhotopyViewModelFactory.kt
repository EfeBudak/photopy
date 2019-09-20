package com.efebudak.photopy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.efebudak.photopy.data.source.PhotosDataSource
import com.efebudak.photopy.ui.main.MainViewModel

class PhotopyViewModelFactory(
    private val photosDataSource: PhotosDataSource
) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return MainViewModel(photosDataSource) as T

    }
}