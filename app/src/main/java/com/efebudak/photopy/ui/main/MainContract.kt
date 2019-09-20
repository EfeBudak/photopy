package com.efebudak.photopy.ui.main

interface MainContract {

    interface View{

    }

    interface Presenter{
        fun searchClicked(searchText:String)
    }

}