package com.efebudak.photopy.ui.search

interface SearchContract {

    interface View{

    }

    interface ViewModel{
        fun searchClicked(searchText:String)
    }

}