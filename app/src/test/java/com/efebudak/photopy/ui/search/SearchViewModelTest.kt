package com.efebudak.photopy.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.efebudak.photopy.TestCoroutineContextProvider
import com.efebudak.photopy.argumentCaptor
import com.efebudak.photopy.data.UiPhoto
import com.efebudak.photopy.data.source.PhotosDataSource
import com.efebudak.photopy.fakeEmptyPhotoListResponse
import com.efebudak.photopy.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class SearchViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Mock
    private lateinit var photosDataSource: PhotosDataSource
    @Mock
    private lateinit var stateHolder: SearchContract.StateHolder

    private val observer: Observer<MutableList<UiPhoto>> = mock()

    lateinit var searchViewModel: SearchViewModel

    @Before
    fun setupMockObjects() {

        MockitoAnnotations.initMocks(this)

        searchViewModel =
            SearchViewModel(photosDataSource, stateHolder, TestCoroutineContextProvider())
        (searchViewModel.uiPhotoList).observeForever(observer)
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun searchClicked_WhileFetchingSearch_DoNothing() {
        `when`(stateHolder.fetchingSearch).thenReturn(true)
        searchViewModel.searchClicked("no matter")

        verify(stateHolder, never()).fetchingSearch = true
    }

    @Test
    fun searchClicked_WhileNOTFetchingSearch_UpdateState() {

        val searchText = "kitten"
        runBlocking {

            `when`(stateHolder.fetchingSearch).thenReturn(false)
            `when`(stateHolder.searchedText).thenReturn(searchText)
            `when`(stateHolder.atPage).thenReturn(1)
            `when`(photosDataSource.fetchPhotoList(searchText, 1)).thenReturn(
                fakeEmptyPhotoListResponse
            )
            launch(Dispatchers.Main) {

                searchViewModel.searchClicked(searchText)

                verify(stateHolder).fetchingSearch = true
                verify(stateHolder).searchedText = searchText
                verify(stateHolder).photoUrlJob?.cancel()
                verify(stateHolder).fetchingPhotoUrl = false

                val inOrderBeforeSuspend = inOrder(stateHolder)

                inOrderBeforeSuspend.verify(stateHolder).biggestRequestedItemIndex = 0
                inOrderBeforeSuspend.verify(stateHolder).atPage = 0
                inOrderBeforeSuspend.verify(stateHolder).lastFetchedItemIndex = 0
                inOrderBeforeSuspend.verify(stateHolder).atPage = 1

                val inOrderAfterSuspend = inOrder(stateHolder)

                inOrderAfterSuspend.verify(stateHolder).atPage = 1
                inOrderAfterSuspend.verify(stateHolder).totalNumberOfPages = 1
                inOrderAfterSuspend.verify(stateHolder).currentTotalPhotoNumber = 0
                inOrderAfterSuspend.verify(stateHolder).totalNumberOfPhotos = 0
                inOrderAfterSuspend.verify(stateHolder).fetchingSearch = false

                val captor = argumentCaptor<MutableList<UiPhoto>>()
                captor.run {
                    verify(observer, times(1)).onChanged(capture())
                    assert(value == emptyList<UiPhoto>())
                }
            }
        }
    }

}
