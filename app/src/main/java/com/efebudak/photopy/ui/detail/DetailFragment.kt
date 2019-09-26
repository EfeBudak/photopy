package com.efebudak.photopy.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.efebudak.photopy.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail.view.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class DetailFragment : Fragment() {

    private val viewModel: DetailContract.ViewModel by inject { parametersOf(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_detail, container, false)

        val args by navArgs<DetailFragmentArgs>()
        val photoId = args.photoId

        viewModel.largePhotoUrl.observe(viewLifecycleOwner) {

            Picasso.with(context)
                .load(it.sourceUrl)
                .placeholder(R.drawable.ic_photo_placeholder)
                .resize(it.width, it.height)
                .centerCrop()
                .into(root.imageViewPhoto)
        }

        viewModel.created(photoId)

        return root
    }
}