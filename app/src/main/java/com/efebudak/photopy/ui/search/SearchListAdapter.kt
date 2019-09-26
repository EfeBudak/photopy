package com.efebudak.photopy.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.efebudak.photopy.R
import com.efebudak.photopy.data.UiPhoto
import com.squareup.picasso.Picasso

class SearchListAdapter(private val photoClickCallback: (photoId: String) -> (Unit)) :
    ListAdapter<UiPhoto, SearchListAdapter.SearchViewHolder>(UiPhotoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_search_result, parent, false)

        return SearchViewHolder(root)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) =
        holder.bindView(getItem(position), photoClickCallback)

    class SearchViewHolder(root: View) : RecyclerView.ViewHolder(root) {

        fun bindView(uiPhoto: UiPhoto, photoClickCallback: (photoId: String) -> (Unit)) {

            itemView.setOnClickListener { photoClickCallback(uiPhoto.id) }

            val imageViewPhoto = itemView.findViewById<ImageView>(R.id.imageViewPhoto)
            val textViewTitle = itemView.findViewById<TextView>(R.id.textViewPhotoTitle)

            if (uiPhoto.photoUrl.isBlank()) {
                imageViewPhoto.setImageResource(R.drawable.ic_face_black_24dp)
            } else {
                Picasso.with(itemView.context)
                    .load(uiPhoto.photoUrl)
                    .placeholder(R.drawable.ic_photo_placeholder)
                    .resize(150, 150)
                    .centerCrop()
                    .into(imageViewPhoto)
            }

            textViewTitle.text = uiPhoto.title

        }
    }
}