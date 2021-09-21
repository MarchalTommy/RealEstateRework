package com.aki.realestatemanagerv2.ui.mainList

import androidx.lifecycle.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.aki.realestatemanagerv2.database.entities.Address
import com.aki.realestatemanagerv2.database.entities.House
import com.aki.realestatemanagerv2.database.entities.relations.HouseAndAddress
import com.aki.realestatemanagerv2.repository.HouseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListViewModel(private val repository: HouseRepository) : ViewModel() {

    val allHousesAndAddresses: LiveData<List<HouseAndAddress>> =
        repository.allHousesAndAddresses.asLiveData()

    fun getFilterQuery(): LiveData<SimpleSQLiteQuery>{
        return repository.filterQuery
    }

    fun filterList(query: SimpleSQLiteQuery): LiveData<List<HouseAndAddress>>{
        return repository.filterEstate(query).asLiveData()
    }

    fun updateHouse(house: House) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateHouse(house)
    }

    fun removeAddress(address: Address) = viewModelScope.launch(Dispatchers.IO) {
        repository.removeAddress(address)
    }

    fun removeHouse(house: House) = viewModelScope.launch(Dispatchers.IO) {
        repository.removeHouse(house)
    }

}
//ViewModel Factory
class ListViewModelFactory(private val repository: HouseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}