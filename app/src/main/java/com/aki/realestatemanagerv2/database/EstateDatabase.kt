package com.aki.realestatemanagerv2.database

import android.content.Context
import android.net.Uri
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aki.realestatemanagerv2.database.dao.HouseDao
import com.aki.realestatemanagerv2.database.entities.Address
import com.aki.realestatemanagerv2.database.entities.Agent
import com.aki.realestatemanagerv2.database.entities.House
import com.aki.realestatemanagerv2.database.entities.Picture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [
        House::class,
        Agent::class,
        Address::class,
        Picture::class
    ],
    version = 1,
    exportSchema = false

)
abstract class EstateDatabase : RoomDatabase() {

    abstract fun houseDao(): HouseDao

    companion object {
        @Volatile
        private var INSTANCE: EstateDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): EstateDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EstateDatabase::class.java,
                    "estate_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(EstateDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class EstateDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.houseDao())
                }
            }
        }

        suspend fun populateDatabase(houseDao: HouseDao) {
            houseDao.deleteAll()
            val uriMansion: String =
                Uri.parse("android.resource://com.aki.realestatemanagerv2/drawable/mansion")
                    .toString()
            val uriVilla1: String =
                Uri.parse("android.resource://com.aki.realestatemanagerv2/drawable/modern_villa")
                    .toString()
            val uriVilla2: String =
                Uri.parse("android.resource://com.aki.realestatemanagerv2/drawable/rich_villa_pool")
                    .toString()
            val uriVilla3: String =
                Uri.parse("android.resource://com.aki.realestatemanagerv2/drawable/villa_pool")
                    .toString()
            val uriVilla4: String =
                Uri.parse("android.resource://com.aki.realestatemanagerv2/drawable/wooden_villa")
                    .toString()
            val uriApartment1: String =
                Uri.parse("android.resource://com.aki.realestatemanagerv2/drawable/apartment")
                    .toString()
            val uriKitchen1: String =
                Uri.parse("android.resource://com.aki.realestatemanagerv2/drawable/kitchen_01")
                    .toString()
            val uriBathroom1: String =
                Uri.parse("android.resource://com.aki.realestatemanagerv2/drawable/bathroom")
                    .toString()
            val uriBedroom1: String =
                Uri.parse("android.resource://com.aki.realestatemanagerv2/drawable/bedroom")
                    .toString()
            val uriLiving1: String =
                Uri.parse("android.resource://com.aki.realestatemanagerv2/drawable/living_r_1")
                    .toString()
            val uriLiving2: String =
                Uri.parse("android.resource://com.aki.realestatemanagerv2/drawable/living_r_2")
                    .toString()
            val uriLiving3: String =
                Uri.parse("android.resource://com.aki.realestatemanagerv2/drawable/living_r_3")
                    .toString()

            val pictures = listOf(
                Picture(uriMansion, "Huge Mansion", 1),
                Picture(uriVilla1, "Beautiful Villa", 5),
                Picture(uriVilla2, "Luxury Villa", 2),
                Picture(uriVilla3, "Simple Villa", 3),
                Picture(uriVilla4, "Pretty wooden Villa", 4),
                Picture(uriApartment1, "Luxurious apartment", 6),
                Picture(uriKitchen1, "Kitchen", 1),
                Picture(uriBathroom1, "Bathroom", 1),
                Picture(uriBedroom1, "Bedroom", 1),
                Picture(uriLiving1, "Living Room", 1),
                Picture(uriLiving2, "Living Room", 2),
                Picture(uriLiving3, "Living Room", 4)
            )

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
            val agents = listOf(
                Agent("Josh", "0601020304", "Josh.Joshy@gmail.com"),
                Agent("Jack", "0602030405", "Jack.Jacky@gmail.com"),
                Agent("Alexandra", "0610203040", "A.lexandra@hotmail.com")
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
                    "27/05/2020", " ", 1, 3, uriMansion
                ),

                House(
                    1325000, "Villa", 650, 12,
                    5, 3, 2, " ", false,
                    schoolAround = false,
                    shopAround = false,
                    museumAround = true,
                    publicPoolAround = false,
                    restaurantAround = false, true,
                    "27/05/2020", " ", 2, 2, uriVilla2
                ),

                House(
                    650000, "Villa", 350, 6,
                    2, 1, 1, " ", false,
                    schoolAround = false,
                    shopAround = true,
                    museumAround = false,
                    publicPoolAround = false,
                    restaurantAround = false, false,
                    "27/05/2020", "29/06/2021", 3, 5, uriVilla3
                ),

                House(
                    1000000, "Villa", 600, 8,
                    3, 2, 2, " ", true,
                    schoolAround = true,
                    shopAround = false,
                    museumAround = false,
                    publicPoolAround = false,
                    restaurantAround = true, true,
                    "27/05/2020", " ", 3, 1, uriVilla4
                ),

                House(
                    735000, "Villa", 250, 5,
                    3, 1, 1, " ", false,
                    schoolAround = false,
                    shopAround = false,
                    museumAround = true,
                    publicPoolAround = true,
                    restaurantAround = false, true,
                    "27/05/2020", " ", 1, 4, uriVilla1
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
                    "15/08/2021",
                    " ",
                    2,
                    6,
                    uriApartment1
                )
            )

            pictures.forEach { houseDao.insertPicture(it) }
            addresses.forEach { houseDao.insertAddress(it) }
            agents.forEach { houseDao.insertAgent(it) }
            houses.forEach { houseDao.insertHouse(it) }
        }
    }
}

