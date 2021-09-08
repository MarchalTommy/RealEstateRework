package com.aki.realestatemanagerv2.database.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.aki.realestatemanagerv2.database.entities.Address
import com.aki.realestatemanagerv2.database.entities.House

data class HouseAndAddress(
    @Embedded val house: House,
    @Relation(
        parentColumn = "houseId",
        entityColumn = "houseId"
    )
    val address: Address
)