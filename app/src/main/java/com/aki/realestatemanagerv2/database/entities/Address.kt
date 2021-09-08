package com.aki.realestatemanagerv2.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Address(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var way: String,
    var complement: String = "",
    var zip: Int,
    var city: String,
    var houseId: Int = 1000
) {


    override fun toString(): String {
        return if (complement.isEmpty()) {
            "$way\n$zip, $city"
        } else {
            "$way\n$complement\n$zip, $city"
        }
    }

    fun toUrlReadyString(): String {
        return way.replace(" ", "+") + "," + city.replace(" ", "+")
    }
}