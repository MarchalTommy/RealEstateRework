package com.aki.realestatemanagerv2.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.aki.realestatemanagerv2.database.entities.relations.HouseAndAddress
import com.aki.realestatemanagerv2.repository.HouseRepository

class MapViewModel(private val repository: HouseRepository) : ViewModel() {

    val allHousesAndAddresses: LiveData<List<HouseAndAddress>> =
        repository.allHousesAndAddresses.asLiveData()

    fun setSelectedHouseId(houseId: Int) {
        repository.setSelectedHouseId(houseId)
    }
}

//ViewModel Factory
class MapViewModelFactory(private val repository: HouseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}