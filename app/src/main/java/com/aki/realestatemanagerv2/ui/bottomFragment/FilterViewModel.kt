package com.aki.realestatemanagerv2.ui.bottomFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.aki.realestatemanagerv2.database.entities.relations.HouseAndAddress
import com.aki.realestatemanagerv2.repository.HouseRepository

class FilterViewModel(private val repository: HouseRepository) : ViewModel() {

    val allHousesAndAddresses: LiveData<List<HouseAndAddress>> =
        repository.allHousesAndAddresses.asLiveData()

    fun updateFilterQuery(query: SimpleSQLiteQuery?){
        repository.filterQuery.value = query
    }

}

//ViewModel Factory
class FilterViewModelFactory(private val repository: HouseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FilterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}