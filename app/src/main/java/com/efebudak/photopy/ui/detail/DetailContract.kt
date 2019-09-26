package com.efebudak.photopy.ui.detail

import androidx.lifecycle.LiveData
import com.efebudak.photopy.data.PhotoSize

interface DetailContract {

    interface ViewModel {
        fun created(photoId: String)
        val largePhotoUrl: LiveData<PhotoSize>
    }
}