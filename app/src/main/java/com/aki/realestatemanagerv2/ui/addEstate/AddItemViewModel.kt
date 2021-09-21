package com.aki.realestatemanagerv2.ui.addEstate

import androidx.lifecycle.*
import com.aki.realestatemanagerv2.database.entities.Address
import com.aki.realestatemanagerv2.database.entities.House
import com.aki.realestatemanagerv2.database.entities.Picture
import com.aki.realestatemanagerv2.repository.HouseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddItemViewModel(private val repository: HouseRepository) : ViewModel() {

    val allHouses: LiveData<List<House>> = repository.allHouses.asLiveData()

    fun insertHouse(house: House) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertHouse(house)
    }

    fun insertAddress(address: Address) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertAddress(address)
    }

    fun insertPicture(picture: Picture) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertPicture(picture)
    }

    fun updateHouse(house: House) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateHouse(house)
    }

    fun updateAddress(address: Address) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateAddress(address)
    }
}

//ViewModel Factory
class AddItemViewModelFactory(private val repository: HouseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddItemViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}