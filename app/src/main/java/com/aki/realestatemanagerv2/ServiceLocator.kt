package com.aki.realestatemanagerv2

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.aki.realestatemanagerv2.database.EstateDatabase
import com.aki.realestatemanagerv2.repository.HouseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

object ServiceLocator {

    private var database: EstateDatabase? = null

    @Volatile
    var houseRepository: HouseRepository? = null
        @VisibleForTesting set

    private val lock = Any()

//    @VisibleForTesting
//    fun resetRepository() {
//        synchronized(lock) {
//            runBlocking {
//
//            }
//        }
//    }

    fun provideHouseRepository(context: Context): HouseRepository {
        synchronized(this) {
            return houseRepository ?: createHouseRepository(context)
        }
    }

    private fun createHouseRepository(context: Context): HouseRepository {
        val newRepo =
            HouseRepository(
                EstateDatabase.getDatabase(context, CoroutineScope(SupervisorJob())).houseDao()
            )
        houseRepository = newRepo
        return newRepo
    }

    private fun createHouseDatabase(context: Context): EstateDatabase {
        val result = EstateDatabase.getDatabase(context, CoroutineScope(SupervisorJob()))
        database = result
        return result
    }
}