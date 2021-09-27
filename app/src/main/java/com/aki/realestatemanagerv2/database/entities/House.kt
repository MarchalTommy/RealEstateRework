@file:Suppress("LocalVariableName")

package com.aki.realestatemanagerv2.database.entities

import android.content.ContentValues
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.NumberFormat
import java.util.*

@Entity
@Parcelize
data class House(
    var price: Int,
    var type: String,
    var size: Int,
    var nbrRooms: Int,
    var nbrBedrooms: Int,
    var nbrBathrooms: Int,
    var nbrPic: Int,
    var description: String,
    var parkAround: Boolean,
    var schoolAround: Boolean,
    var shopAround: Boolean,
    var museumAround: Boolean,
    var publicPoolAround: Boolean,
    var restaurantAround: Boolean,
    var stillAvailable: Boolean,
    var dateEntryOnMarket: Long,
    var dateSell: Long,
    var agentId: Int,
    var addressId: Int?,
    var mainUri: String?
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var houseId: Int = 0

    fun currencyFormatUS(amount: Int = price): String {
        val COUNTRY = "US"
        val LANGUAGE = "en"

        return NumberFormat.getCurrencyInstance(Locale(LANGUAGE, COUNTRY)).format(amount)
    }

    fun currencyFormatFR(amount: Int = price): String {
        val COUNTRY = "FR"
        val LANGUAGE = "fr"

        return NumberFormat.getCurrencyInstance(Locale(LANGUAGE, COUNTRY)).format(amount)
    }

    // UTILS
    companion object {
        fun fromContentValues(values: ContentValues?): House{
            val house = House(
                0, " ", 0, 0, 0, 0, 0, " ",
                parkAround = false,
                schoolAround = false,
                shopAround = false,
                museumAround = false,
                publicPoolAround = false,
                restaurantAround = false,
                true, 0L, 0L, 1, null, " "
            )
            if(values?.containsKey("price") == true) house.price = values.getAsInteger("price")
            if(values?.containsKey("type") == true) house.type = values.getAsString("type")
            if(values?.containsKey("size") == true) house.size = values.getAsInteger("size")
            if(values?.containsKey("nbrRooms") == true) house.nbrRooms = values.getAsInteger("nbrRooms")
            if(values?.containsKey("nbrBedrooms") == true) house.nbrBedrooms = values.getAsInteger("nbrBedrooms")
            if(values?.containsKey("nbrBathrooms") == true) house.nbrBathrooms = values.getAsInteger("nbrBathrooms")
            if(values?.containsKey("nbrPic") == true) house.nbrPic = values.getAsInteger("nbrPic")
            if(values?.containsKey("description") == true) house.description = values.getAsString("description")
            if(values?.containsKey("parkAround") == true) house.parkAround = values.getAsBoolean("parkAround")
            if(values?.containsKey("schoolAround") == true) house.schoolAround = values.getAsBoolean("schoolAround")
            if(values?.containsKey("shopAround") == true) house.shopAround = values.getAsBoolean("shopAround")
            if(values?.containsKey("museumAround") == true) house.museumAround = values.getAsBoolean("museumAround")
            if(values?.containsKey("publicPoolAround") == true) house.publicPoolAround = values.getAsBoolean("publicPoolAround")
            if(values?.containsKey("restaurantAround") == true) house.restaurantAround = values.getAsBoolean("restaurantAround")
            if(values?.containsKey("stillAvailable") == true) house.stillAvailable = values.getAsBoolean("stillAvailable")
            if(values?.containsKey("dateEntry") == true) house.dateEntryOnMarket = values.getAsLong("dateEntry")
            if(values?.containsKey("dateSold") == true) house.dateSell = values.getAsLong("dateSold")
            if(values?.containsKey("agentId") == true) house.agentId = values.getAsInteger("agentId")
            if(values?.containsKey("addressId") == true) house.addressId = values.getAsInteger("addressId")
            if(values?.containsKey("mainPicUri") == true) house.mainUri = values.getAsString("mainPicUri")
            if(values?.containsKey("houseId") == true) house.houseId = values.getAsInteger("houseId")
            return house
        }
    }
}