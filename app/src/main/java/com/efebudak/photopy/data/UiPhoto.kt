package com.efebudak.photopy.data

data class UiPhoto(val id: String, val title: String, val photoUrl: String = "") {

  constructor(uiPhoto: UiPhoto) : this(uiPhoto.id, uiPhoto.title, uiPhoto.photoUrl)
}