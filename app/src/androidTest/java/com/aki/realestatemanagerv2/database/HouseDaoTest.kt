package com.aki.realestatemanagerv2.database

import android.content.ContentValues.TAG
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.aki.realestatemanagerv2.Utils
import com.aki.realestatemanagerv2.database.entities.Address
import com.aki.realestatemanagerv2.database.entities.Agent
import com.aki.realestatemanagerv2.database.entities.House
import com.aki.realestatemanagerv2.database.entities.relations.HouseAndAddress
import com.aki.realestatemanagerv2.getOrAwaitValue
import junit.framework.Assert.assertNull
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class HouseDaoTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: EstateDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            EstateDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertEstateAndGetById() {
        val house = House(
            100,
            "House",
            10,
            1,
            1,
            1,
            0,
            "Empty Description",
            parkAround = false,
            schoolAround = false,
            shopAround = false,
            museumAround = false,
            publicPoolAround = false,
            restaurantAround = false,
            stillAvailable = true,
            dateEntryOnMarket = Utils.getTimestampFromDate(Utils.getTodayDate()),
            dateSell = 0,
            agentId = 1,
            addressId = 99,
            mainUri = null
        )
        house.houseId = 42
        database.houseDao().insertHouse(house)

        val loaded =
            database.houseDao().getHouseWithId(house.houseId).asLiveData().getOrAwaitValue()
        assertThat(loaded.size, `is`(house.size))
        assertThat(loaded.price, `is`(house.price))
        assertThat(loaded.type, `is`(house.type))
    }

    @Test
    fun updateHouseAndGetById() {
        val house = House(
            1000,
            "House",
            100,
            1,
            1,
            1,
            0,
            "Empty Description",
            parkAround = false,
            schoolAround = false,
            shopAround = false,
            museumAround = false,
            publicPoolAround = false,
            restaurantAround = false,
            stillAvailable = true,
            dateEntryOnMarket = Utils.getTimestampFromDate(Utils.getTodayDate()),
            dateSell = 0,
            agentId = 1,
            addressId = 99,
            mainUri = null
        )
        house.houseId = 42
        database.houseDao().insertHouse(house)

        house.price = 42000
        house.type = "Villa"
        house.size = 350
        database.houseDao().updateHouse(house)

        val loaded =
            database.houseDao().getHouseWithId(house.houseId).asLiveData().getOrAwaitValue()

        assertThat(loaded.size, `is`(house.size))
        assertThat(loaded.size, `is`(350))
        assertThat(loaded.price, `is`(house.price))
        assertThat(loaded.price, `is`(42000))
        assertThat(loaded.type, `is`(house.type))
        assertThat(loaded.type, `is`("Villa"))
    }

    @Test
    fun insertHouseAndAddressAndGetAddressFromHouse() {
        val house = House(
            100,
            "House",
            10,
            1,
            1,
            1,
            0,
            "Empty Description",
            parkAround = false,
            schoolAround = false,
            shopAround = false,
            museumAround = false,
            publicPoolAround = false,
            restaurantAround = false,
            stillAvailable = true,
            dateEntryOnMarket = Utils.getTimestampFromDate(Utils.getTodayDate()),
            dateSell = 0,
            agentId = 1,
            addressId = 42,
            mainUri = null
        )
        house.houseId = 42
        val address = Address(42, "72 Square de la Couronne", "", 75001, "Paris", house.houseId)
        database.houseDao().insertAddress(address)
        database.houseDao().insertHouse(house)

        var loaded: HouseAndAddress
        database.houseDao().getHouseAndAddress(house.houseId).asLiveData().observeForever {
            if (it != null) {
                loaded = it
                assertThat(loaded.house.size, `is`(house.size))
                assertThat(loaded.house.price, `is`(house.price))
                assertThat(loaded.house.type, `is`(house.type))
                assertThat(loaded.address.city, `is`(address.city))
                assertThat(loaded.address.way, `is`(address.way))
                assertThat(loaded.address.zip, `is`(address.zip))
                assertThat(loaded.address.houseId, `is`(house.houseId))
            }
        }
    }

    @Test
    fun removeHouse() {
        val house = House(
            100,
            "House",
            10,
            1,
            1,
            1,
            0,
            "Empty Description",
            parkAround = false,
            schoolAround = false,
            shopAround = false,
            museumAround = false,
            publicPoolAround = false,
            restaurantAround = false,
            stillAvailable = true,
            dateEntryOnMarket = Utils.getTimestampFromDate(Utils.getTodayDate()),
            dateSell = 0,
            agentId = 1,
            addressId = 99,
            mainUri = null
        )
        house.houseId = 42
        database.houseDao().insertHouse(house)

        val loaded =
            database.houseDao().getHouseWithId(house.houseId).asLiveData().getOrAwaitValue()
        assertThat(loaded.size, `is`(house.size))
        assertThat(loaded.price, `is`(house.price))
        assertThat(loaded.type, `is`(house.type))

        database.houseDao().removeHouse(house)

        val removed =
            database.houseDao().getHouseWithId(house.houseId).asLiveData().getOrAwaitValue()
        assertNull(removed)
    }

    @Test
    fun filterHouses() {
        // Init database with a list of estate
        val addresses = listOf(
            Address(
                0, "13 Woodstone Dr", "", 70471, "Mandeville",
                houseId = 1
            ),
            Address(
                0, "22358 Crane St", "", 70449, "Maurepas", 3
            ),
            Address(
                0, "951 7th St", "", 70767, "Port Allen", 4
            ),
            Address(
                0, "121 Pailet Dr", "", 70058, "Harvey", 2
            ),
            Address(
                0, "137 Myia Ln", "", 70517, "Breaux Bridge", 5
            ),
            Address(
                0, "2420 N Claiborne Ave", "", 70117, "New Orleans", 6
            )
        )
        val houses = listOf(
            House(
                81450000, "Mansion", 1250, 21,
                8, 5, 5, " ", true,
                schoolAround = true,
                shopAround = true,
                museumAround = false,
                publicPoolAround = true,
                restaurantAround = false, true,
                Utils.getTimestampFromDate("27/05/2020"), 0L, 1, 3,
                null
            ),

            House(
                1325000, "Villa", 650, 12,
                5, 3, 2, " ", false,
                schoolAround = false,
                shopAround = false,
                museumAround = true,
                publicPoolAround = false,
                restaurantAround = false, true,
                Utils.getTimestampFromDate("27/05/2020"), 0L, 2, 2,
                null
            ),

            House(
                650000,
                "Villa",
                350,
                6,
                2,
                1,
                1,
                " ",
                false,
                schoolAround = false,
                shopAround = true,
                museumAround = false,
                publicPoolAround = false,
                restaurantAround = false,
                false,
                Utils.getTimestampFromDate("27/05/2020"),
                Utils.getTimestampFromDate("29/06/2021"),
                3,
                5,
                null
            ),

            House(
                1000000, "Villa", 600, 8,
                3, 2, 2, " ", true,
                schoolAround = true,
                shopAround = false,
                museumAround = false,
                publicPoolAround = false,
                restaurantAround = true, true,
                Utils.getTimestampFromDate("27/05/2020"), 0L, 3, 1,
                null
            ),

            House(
                735000, "Villa", 250, 5,
                3, 1, 1, " ", false,
                schoolAround = false,
                shopAround = false,
                museumAround = true,
                publicPoolAround = true,
                restaurantAround = false, true,
                Utils.getTimestampFromDate("27/05/2020"), 0L, 1, 4,
                null
            ),

            House(
                35000000,
                "Apartment",
                450,
                6,
                2,
                2,
                1,
                "This luxurious apartment is located somewhere! Gorgeous with a killer view, this is the place to be!" +
                        "\nSorry tho, I ain't no estate agent, I don't know how to sell an estate!",
                false,
                schoolAround = true,
                shopAround = false,
                museumAround = true,
                publicPoolAround = false,
                restaurantAround = false,
                true,
                Utils.getTimestampFromDate("15/08/2021"),
                0L,
                2,
                6,
                null
            )
        )
        addresses.forEach { database.houseDao().insertAddress(it) }
        houses.forEach { database.houseDao().insertHouse(it) }

        // Checking just to make sure database is not empty
        val fullList = database.houseDao().getAllHouses().asLiveData().getOrAwaitValue()
        assertTrue(fullList.size == 6)

        // Should return only the mansion with ID 1, 81.450.000$
        val queryString = "SELECT * FROM house WHERE price BETWEEN 50200000 AND 100000000 AND size BETWEEN 1050 AND 2000 AND nbrRooms BETWEEN 1 AND 21 AND nbrBedrooms BETWEEN 1 AND 8 AND nbrBathrooms BETWEEN 1 AND 5 AND type = \"Mansion\" AND schoolAround = 1"
        val query = SimpleSQLiteQuery(queryString)

        val filtered = database.houseDao().filterEstates(query).asLiveData().getOrAwaitValue(5)

        assertTrue(filtered.size == 1)
        assertTrue(filtered[0].house.price == 81450000)
        assertTrue(filtered[0].house.schoolAround)
        assertTrue(filtered[0].house.parkAround)
        assertTrue(filtered[0].address.city == "Mandeville")
    }

}