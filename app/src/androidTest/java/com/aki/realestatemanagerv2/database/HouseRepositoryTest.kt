package com.aki.realestatemanagerv2.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.aki.realestatemanagerv2.Utils
import com.aki.realestatemanagerv2.database.entities.House
import com.aki.realestatemanagerv2.repository.HouseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class HouseRepositoryTest {

    private lateinit var repository: HouseRepository
    private lateinit var database: EstateDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            EstateDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        repository =
            HouseRepository(database.houseDao())
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun insertEstateAndGetById() = runBlockingTest {
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
        repository.insertHouse(house)

        var loaded: House
        repository.getHouseWithId(house.houseId).asLiveData().observeForever {
            if(it != null) {
                loaded = it
                ViewMatchers.assertThat(loaded.size, Is.`is`(house.size))
                ViewMatchers.assertThat(loaded.price, Is.`is`(house.price))
                ViewMatchers.assertThat(loaded.type, Is.`is`(house.type))
            }
        }
    }

}