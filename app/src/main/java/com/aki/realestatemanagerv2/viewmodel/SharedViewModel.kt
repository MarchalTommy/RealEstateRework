package com.aki.realestatemanagerv2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val elementClicked = MutableLiveData<Transition>()

    fun setIsClicked(transition: Transition){
        elementClicked.postValue(transition)
    }
}

enum class Transition{
    LIST_DETAIL, DETAIL_LIST, LIST_ADD, ADD_LIST, DETAIL_EDIT, EDIT_DETAIL
}