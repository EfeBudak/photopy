package com.efebudak.photopy.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.efebudak.photopy.R
import com.efebudak.photopy.data.UiPhoto
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

private const val NUMBER_OF_COLUMNS = 2

class SearchFragment : Fragment() {

  private val viewModel: SearchContract.ViewModel by inject { parametersOf(this) }

  private lateinit var viewAdapter: SearchListAdapter
  private lateinit var viewManager: GridLayoutManager

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    val root = inflater.inflate(R.layout.fragment_search, container, false)

    viewAdapter = SearchListAdapter {
      if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
        val directionToDetail =
          SearchFragmentDirections.actionSearchFragmentToDetailFragment(it)
        findNavController().navigate(directionToDetail)
      }
    }
    viewManager = GridLayoutManager(context, NUMBER_OF_COLUMNS)

    root.recyclerViewSearchResults.let {
      it.adapter = viewAdapter
      it.layoutManager = viewManager

      it.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
          super.onScrolled(recyclerView, dx, dy)

          viewModel.lastVisibleItemPosition(viewManager.findLastVisibleItemPosition())
        }
      })
    }

    root.editTextSearch.setOnEditorActionListener { textView, actionId, _ ->
      return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_SEARCH) {

        recyclerViewSearchResults.scrollToPosition(0)
        viewModel.searchClicked(textView.text.toString())
        true
      } else {
        false
      }
    }

    return root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewModel.uiPhotoList.observe(viewLifecycleOwner) {

      val newList = mutableListOf<UiPhoto>()
      it.forEach { uiPhotoItem -> newList.add(UiPhoto(uiPhotoItem)) }
      viewAdapter.submitList(newList)
    }

    viewModel.searchLoading.observe(viewLifecycleOwner) {

      if (it) {
        progressBarLoading.visibility = View.VISIBLE
        recyclerViewSearchResults.visibility = View.GONE
      } else {
        progressBarLoading.visibility = View.GONE
        recyclerViewSearchResults.visibility = View.VISIBLE
      }
    }
  }

}