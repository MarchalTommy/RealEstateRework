package com.aki.realestatemanagerv2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aki.realestatemanagerv2.database.entities.House

class SharedViewModel : ViewModel() {
    val elementClicked = MutableLiveData<Transition>()

    val estateId = MutableLiveData<Int>()

    fun setIsClicked(transition: Transition){
        elementClicked.postValue(transition)
    }

    fun setHouse(houseId: Int) {
        estateId.postValue(houseId)
    }
}

enum class Transition{
    LIST_DETAIL, DETAIL_LIST, LIST_ADD, ADD_LIST, DETAIL_EDIT, EDIT_DETAIL
}