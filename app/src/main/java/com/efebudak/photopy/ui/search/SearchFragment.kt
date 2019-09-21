package com.efebudak.photopy.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.efebudak.photopy.R
import kotlinx.android.synthetic.main.fragment_search.view.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import kotlin.time.ExperimentalTime

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by inject { parametersOf(this) }

    private lateinit var viewAdapter: SearchListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    @ExperimentalTime
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_search, container, false)

        viewModel.uiPhotoMutableList.observe(this) {

            Log.d("uiPhotoMutableList", "Size ${it.size}")

            viewAdapter.updateUiPhotoList(it)
        }



        viewAdapter = SearchListAdapter(emptyList())
        viewManager = GridLayoutManager(context, 2)

        root.recyclerViewSearchResults.let {
            it.adapter = viewAdapter
            it.layoutManager = viewManager
        }

        root.editTextSearch.setOnEditorActionListener { textView, actionId, event ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                viewModel.searchClicked(textView.text.toString())
                true
            } else {
                false
            }
        }

        return root
    }

}