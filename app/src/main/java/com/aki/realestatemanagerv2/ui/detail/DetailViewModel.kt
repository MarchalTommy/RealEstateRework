package com.aki.realestatemanagerv2.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.aki.realestatemanagerv2.database.entities.Agent
import com.aki.realestatemanagerv2.database.entities.Picture
import com.aki.realestatemanagerv2.database.entities.relations.HouseAndAddress
import com.aki.realestatemanagerv2.repository.HouseRepository

class DetailViewModel(private val repository: HouseRepository) : ViewModel() {

    fun setSelectedHouseId(houseId: Int) {
        return repository.setSelectedHouseId(houseId)
    }

    fun getSelectedHouseId(): LiveData<Int> {
        return repository.selectedHouseId
    }

    fun getHouseAndAddress(houseId: Int): LiveData<HouseAndAddress> {
        return repository.getHouseAndAddress(houseId).asLiveData()
    }

    fun getPictures(houseId: Int): LiveData<List<Picture>> {
        return repository.getPictures(houseId).asLiveData()
    }

    fun getStaticMap(address: String, api: String): String {
        return repository.getStaticMap(address, api)
    }

    fun getAgent(agentId: Int): LiveData<Agent> {
        return repository.getAgent(agentId).asLiveData()
    }

}

//ViewModel Factory
class DetailViewModelFactory(private val repository: HouseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}