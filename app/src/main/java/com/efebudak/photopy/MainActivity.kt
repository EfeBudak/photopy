package com.efebudak.photopy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.efebudak.photopy.data.source.remote.PhotosRemoteDataSource
import com.efebudak.photopy.network.FlickrService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val photosRemoteDataSource = PhotosRemoteDataSource()

        GlobalScope.launch {

            withContext(Dispatchers.Main) {

                val a = photosRemoteDataSource.fetchPhotoList("kitten")

                textView.text = "totalPage ${a.photos.pages}"
            }
        }
    }
}
