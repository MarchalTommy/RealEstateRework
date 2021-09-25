package com.aki.realestatemanagerv2.ui.edit

import androidx.lifecycle.*
import com.aki.realestatemanagerv2.database.entities.Address
import com.aki.realestatemanagerv2.database.entities.House
import com.aki.realestatemanagerv2.database.entities.Picture
import com.aki.realestatemanagerv2.database.entities.relations.HouseAndAddress
import com.aki.realestatemanagerv2.repository.HouseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditItemViewModel(private val repository: HouseRepository) : ViewModel() {

    fun getSelectedEstateId(): LiveData<Int> {
        return repository.selectedHouseId
    }

    fun getHouseAndAddress(houseId: Int): LiveData<HouseAndAddress> {
        return repository.getHouseAndAddress(houseId).asLiveData()
    }

    fun getPictures(houseId: Int): LiveData<List<Picture>> {
        return repository.getPictures(houseId).asLiveData()
    }

    fun updateHouse(house: House) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateHouse(house)
    }

    fun updateAddress(address: Address) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateAddress(address)
    }

    fun insertPicture(picture: Picture) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertPicture(picture)
    }

    fun removePicture(picture: Picture) = viewModelScope.launch(Dispatchers.IO) {
        repository.removePicture(picture)
    }
}

//ViewModel Factory
class EditItemViewModelFactory(private val repository: HouseRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditItemViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}