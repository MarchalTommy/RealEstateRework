package com.aki.realestatemanagerv2.database.entities

import android.os.Parcelable
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
    val agentId: Int,
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
}