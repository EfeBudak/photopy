package com.efebudak.photopy.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.efebudak.photopy.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail.view.*

class DetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_detail, container, false)

        val args by navArgs<DetailFragmentArgs>()
        val photoUrl = args.imageUrl

        Picasso.with(context)
            .load(photoUrl)
            .placeholder(R.drawable.ic_photo_placeholder)
            .resize(150, 150)
            .centerCrop()
            .into(root.imageViewPhoto)


        return root
    }
}