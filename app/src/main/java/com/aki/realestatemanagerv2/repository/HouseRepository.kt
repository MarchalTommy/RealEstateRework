package com.aki.realestatemanagerv2.repository

import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.aki.realestatemanagerv2.database.dao.HouseDao
import com.aki.realestatemanagerv2.database.entities.Address
import com.aki.realestatemanagerv2.database.entities.Agent
import com.aki.realestatemanagerv2.database.entities.House
import com.aki.realestatemanagerv2.database.entities.Picture
import com.aki.realestatemanagerv2.database.entities.relations.HouseAndAddress
import kotlinx.coroutines.flow.Flow

class HouseRepository(private val houseDao: HouseDao) {

    val filterQuery: MutableLiveData<SimpleSQLiteQuery> = MutableLiveData<SimpleSQLiteQuery>()

    // region GETTERS
    val allHouses: Flow<List<House>> = houseDao.getAllHouses()

    val allAddresses: Flow<List<Address>> = houseDao.getAllAddresses()

    val allHousesAndAddresses: Flow<List<HouseAndAddress>> = houseDao.getAllHousesAndAddresses()

    fun filterEstate(query: SimpleSQLiteQuery): Flow<List<HouseAndAddress>> {
        return houseDao.filterEstates(query = query)
    }

    fun getHouseAndAddress(houseId: Int): Flow<HouseAndAddress> {
        return houseDao.getHouseAndAddress(houseId)
    }

    fun getHouseWithId(id: Int): Flow<House> {
        return houseDao.getHouseWithId(id)
    }

    fun getHouseFromAddressId(addressId: Int): Flow<House> {
        return houseDao.getHouseFromAddressId(addressId)
    }

    fun getAddressFromHouse(houseId: Int): Flow<Address> {
        return houseDao.getAddressFromHouse(houseId)
    }

    fun getAgent(agentId: Int): Flow<Agent> {
        return houseDao.getAgent(agentId)
    }

    fun getPictures(houseId: Int): Flow<List<Picture>> {
        return houseDao.getPicturesFromHouse(houseId)
    }

    fun getStaticMap(address: String, api: String): String {
        return "https://maps.googleapis.com/maps/api/staticmap?center=$address&zoom=15&size=300x300&scale=3&markers=color:red|$address&key=${api}"
    }
    // endregion GETTERS

    // region INSERTS

    suspend fun insertHouse(house: House) {
        houseDao.insertHouse(house)
    }

    suspend fun insertAddress(address: Address) {
        houseDao.insertAddress(address)
    }

    suspend fun insertAgent(agent: Agent) {
        houseDao.insertAgent(agent)
    }

    suspend fun insertPicture(picture: Picture) {
        houseDao.insertPicture(picture)
    }
    // endregion INSERTS

    // region UPDATE
    fun updateHouse(house: House) {
        houseDao.updateHouse(house)
    }

    fun updateAddress(address: Address) {
        houseDao.updateAddress(address)
    }
    // endregion UPDATE

    // region DELETE
    fun removePicture(picture: Picture) {
        houseDao.removePicture(picture)
    }

    fun removeAddress(address: Address) {
        houseDao.removeAddress(address)
    }

    fun removeHouse(house: House) {
        houseDao.removeHouse(house)
    }

    fun removeAllHouses() {
        houseDao.deleteAllHouses()
    }

    fun removeAllAddresses() {
        houseDao.deleteAllAddresses()
    }
    // endregion DELETE


}