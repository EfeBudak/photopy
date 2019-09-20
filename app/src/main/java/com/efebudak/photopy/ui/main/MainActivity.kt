package com.efebudak.photopy.ui.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.efebudak.photopy.R
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import kotlin.time.ExperimentalTime

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by inject { parametersOf(this) }

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.uiPhotoMutableList.observe(this) {

            Log.d("uiPhotoMutableList", "Size ${it.size}")
            textView.text = "totalsize ${it.size}"
        }

        button.setOnClickListener {
            viewModel.searchClicked("kitten")
        }

    }
}
