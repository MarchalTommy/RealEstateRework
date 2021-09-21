package com.aki.realestatemanagerv2.database.dao

import android.database.Cursor
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
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
    fun insertHouse(house: House): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAddress(address: Address)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgent(agent: Agent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPicture(picture: Picture)

    //endregion INSERT

    //region QUERY
    @Query("SELECT * FROM house WHERE houseId = :houseId")
    fun getHouseWithCursor(houseId: Int): Cursor

    @Query("DELETE FROM house")
    fun deleteAllHouses()

    @Query("DELETE FROM address")
    fun deleteAllAddresses()

    @Query("SELECT * FROM house WHERE houseId = :houseId")
    fun getHouseWithId(houseId: Int): Flow<House>

    @Query("SELECT * FROM address WHERE zip != 0")
    fun getAllAddresses(): Flow<List<Address>>

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
    fun getHouseAndAddress(houseId: Int): Flow<HouseAndAddress>

    @Transaction
    @Query("SELECT * FROM house")
    fun getAllHousesAndAddresses(): Flow<List<HouseAndAddress>>

    @Transaction
    @RawQuery
    fun filterEstates(query: SimpleSQLiteQuery): Flow<List<HouseAndAddress>>
    //endregion QUERY

    //region UPDATE
    @Update
    fun updateHouse(house: House): Int

    @Update
    fun updateAddress(address: Address)
    //endregion UPDATE

    //region DELETE
    @Delete
    fun removePicture(picture: Picture)

    @Delete
    fun removeHouse(house: House): Int

    @Query("DELETE FROM house WHERE houseId = :houseId")
    fun removeHouseById(houseId: Int): Int

    @Delete
    fun removeAddress(address: Address)
    //endregion DELETE
}