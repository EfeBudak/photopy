package com.efebudak.photopy.ui.search

import androidx.recyclerview.widget.DiffUtil
import com.efebudak.photopy.data.UiPhoto

class UiPhotoDiffCallback : DiffUtil.ItemCallback<UiPhoto>() {
    override fun areItemsTheSame(oldItem: UiPhoto, newItem: UiPhoto): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: UiPhoto, newItem: UiPhoto): Boolean =
        oldItem == newItem
}