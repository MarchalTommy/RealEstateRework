package com.aki.realestatemanagerv2.database.entities

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Picture (
    @PrimaryKey
    val uri: String,
    val title: String,
    val houseId: Int
)