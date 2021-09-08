package com.aki.realestatemanagerv2.viewmodel

import androidx.lifecycle.*
import com.aki.realestatemanagerv2.database.entities.Address
import com.aki.realestatemanagerv2.database.entities.Agent
import com.aki.realestatemanagerv2.database.entities.House
import com.aki.realestatemanagerv2.database.entities.Picture
import com.aki.realestatemanagerv2.database.entities.relations.HouseAndAddress
import com.aki.realestatemanagerv2.repository.HouseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HouseViewModel(private val repository: HouseRepository) : ViewModel() {

    // region GETTERS
    val allHouses: LiveData<List<House>> = repository.allHouses.asLiveData()

    val allAddresses: LiveData<List<Address>> = repository.allAddresses.asLiveData()

    val allHousesAndAddresses: LiveData<List<HouseAndAddress>> =
        repository.allHousesAndAddresses.asLiveData()

    fun getHouseAndAddress(houseId: Int): LiveData<List<HouseAndAddress>> {
        return repository.getHouseAndAddress(houseId).asLiveData()
    }

    fun getHouseWithId(houseId: Int): LiveData<House> {
        return repository.getHouseWithId(houseId).asLiveData()
    }

    fun getHouseFromAddressId(addressId: Int): LiveData<House> {
        return repository.getHouseFromAddressId(addressId).asLiveData()
    }

    fun getAddressFromHouse(houseId: Int): LiveData<Address> {
        return repository.getAddressFromHouse(houseId).asLiveData()
    }

    fun getAgent(agentId: Int): LiveData<Agent> {
        return repository.getAgent(agentId).asLiveData()
    }

    fun getPictures(houseId: Int): LiveData<List<Picture>> {
        return repository.getPictures(houseId).asLiveData()
    }

    fun getStaticMap(address: String, api: String): String {
        return repository.getStaticMap(address, api)
    }

    fun searchHouseAndAddress(
        priceMax: Int = 999999999,
        priceMin: Int = 0,
        sizeMax: Int = 999999999,
        sizeMin: Int = 0,
        roomMax: Int = 1000,
        roomMin: Int = 1,
        dateEntry: String,
        dateSold: String,
        nbrPic: Int,
        maxPicNNbr: Int = 1000,
        bedroomMax: Int = 1000,
        bedroomMin: Int = 1,
        bathroomMax: Int = 1000,
        bathroomMin: Int = 1,
        type: String = "Villa",
        park: Int = 1,
        pool: Int = 1,
        school: Int = 1,
        museum: Int = 1,
        restaurant: Int = 1,
        shop: Int = 1
    ): LiveData<List<HouseAndAddress>> {
        return repository.searchHouseAndAddress(
            priceMax,
            priceMin,
            sizeMax,
            sizeMin,
            roomMax,
            roomMin,
            dateEntry,
            dateSold,
            nbrPic,
            maxPicNNbr,
            bedroomMax,
            bedroomMin,
            bathroomMax,
            bathroomMin,
            type,
            park,
            pool,
            school,
            museum,
            restaurant,
            shop
        ).asLiveData()
    }
    // endregion GETTERS

    // region INSERTS

    fun insertHouse(house: House) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertHouse(house)
    }

    fun insertAddress(address: Address) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertAddress(address)
    }

    fun insertAgent(agent: Agent) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertAgent(agent)
    }

    fun insertPicture(picture: Picture) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertPicture(picture)
    }
    // endregion INSERTS

    // region UPDATE
    fun updateHouse(house: House) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateHouse(house)
    }

    fun updateAddress(address: Address) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateAddress(address)
    }

    // endregion UPDATE

    // region DELETE
    fun removePicture(picture: Picture) = viewModelScope.launch(Dispatchers.IO) {
        repository.removePicture(picture)
    }

    fun removeAddress(address: Address) = viewModelScope.launch(Dispatchers.IO) {
        repository.removeAddress(address)
    }

    fun removeHouse(house: House) = viewModelScope.launch(Dispatchers.IO) {
        repository.removeHouse(house)
    }
    // endregion DELETE
}

//ViewModel Factory
class HouseViewModelFactory(private val repository: HouseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HouseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HouseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}