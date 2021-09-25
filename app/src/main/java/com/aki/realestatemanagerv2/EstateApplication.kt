package com.aki.realestatemanagerv2

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.aki.realestatemanagerv2.database.EstateDatabase
import com.aki.realestatemanagerv2.repository.HouseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/*
TODO : TESTS
 */
class EstateApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { EstateDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { HouseRepository(database.houseDao()) }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}