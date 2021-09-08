package com.aki.realestatemanagerv2.database.dao

import androidx.room.*
import com.aki.realestatemanagerv2.database.entities.Address
import com.aki.realestatemanagerv2.database.entities.Agent
import com.aki.realestatemanagerv2.database.entities.House
import com.aki.realestatemanagerv2.database.entities.Picture
import com.aki.realestatemanagerv2.database.entities.relations.HouseAndAddress
import kotlinx.coroutines.flow.Flow

@Dao
interface HouseDao {

    //region INSERT
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHouse(house: House)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: Address)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgent(agent: Agent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPicture(picture: Picture)

    //endregion INSERT

    //region QUERY
    @Query("DELETE FROM house")
    suspend fun deleteAll()

    @Query("SELECT * FROM house WHERE houseId = :houseId")
    fun getHouseWithId(houseId: Int): Flow<House>

    @Query("SELECT * FROM address WHERE zip != 0")
    fun getAllAddresses(): Flow<List<Address>>

    @Query("SELECT * FROM House WHERE agentId = :agentId")
    suspend fun getHousesOfAgent(agentId: Int): List<House>

    @Query("SELECT * FROM house WHERE type = :type")
    suspend fun getHouseOfType(type: String): List<House>

    @Query("SELECT * FROM house WHERE addressId = :addressId")
    fun getHouseFromAddressId(addressId: Int): Flow<House>

    @Query("SELECT * FROM agent WHERE id = :agentId")
    fun getAgent(agentId: Int): Flow<Agent>

    @Query("SELECT * FROM house")
    fun getAllHouses(): Flow<List<House>>

    @Query("SELECT * FROM address WHERE houseId = :houseId")
    fun getAddressFromHouse(houseId: Int): Flow<Address>

    @Query("SELECT * FROM picture WHERE houseId = :houseId")
    fun getPicturesFromHouse(houseId: Int): Flow<List<Picture>>

    // TRANSACTION HOUSE ADDRESS
    @Transaction
    @Query("SELECT * FROM house WHERE houseId = :houseId")
    fun getHouseAndAddress(houseId: Int): Flow<List<HouseAndAddress>>

    @Transaction
    @Query("SELECT * FROM house")
    fun getAllHousesAndAddresses(): Flow<List<HouseAndAddress>>

    @Transaction
    @Query("SELECT * FROM house WHERE price BETWEEN :priceMin AND :priceMax AND size BETWEEN :sizeMin AND :sizeMax AND nbrRooms BETWEEN :roomMin AND :roomMax AND nbrBedrooms BETWEEN :bedroomMin AND :bedroomMax AND nbrBathrooms BETWEEN :bathroomMin AND :bathroomMax AND type = :type AND dateEntryOnMarket = :dateEntry AND dateSell = :dateSold AND nbrPic BETWEEN :nbrPic AND :maxPicNNbr AND (parkAround = :park AND :park == 1) OR (schoolAround = :school AND :school == 1) OR (restaurantAround = :restaurant AND :restaurant == 1) OR (shopAround = :shop AND :shop == 1) OR (museumAround = :museum AND :museum == 1) OR (publicPoolAround = :pool AND :pool == 1)")
    fun searchHousesAndAddresses(
        priceMax: Int,
        priceMin: Int,
        sizeMax: Int,
        sizeMin: Int,
        roomMax: Int,
        roomMin: Int,
        dateEntry: String,
        dateSold: String,
        nbrPic: Int,
        maxPicNNbr: Int,
        bedroomMax: Int,
        bedroomMin: Int,
        bathroomMax: Int,
        bathroomMin: Int,
        type: String,
        park: Int,
        pool: Int,
        school: Int,
        museum: Int,
        restaurant: Int,
        shop: Int
    ): Flow<List<HouseAndAddress>>
    //endregion QUERY

    //region UPDATE
    @Update
    fun updateHouse(house: House)

    @Update
    fun updateAddress(address: Address)
    //endregion UPDATE

    //region DELETE
    @Delete
    fun removePicture(picture: Picture)

    @Delete
    fun removeHouse(house: House)

    @Delete
    fun removeAddress(address: Address)
    //endregion DELETE
}