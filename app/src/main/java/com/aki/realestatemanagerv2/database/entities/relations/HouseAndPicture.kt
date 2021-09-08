package com.aki.realestatemanagerv2.database.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.aki.realestatemanagerv2.database.entities.House
import com.aki.realestatemanagerv2.database.entities.Picture

data class HouseAndPicture (
    @Embedded val house: House,
    @Relation(
        parentColumn = "houseId",
        entityColumn = "houseId"
    )
    val picture: Picture
)