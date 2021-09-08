package com.aki.realestatemanagerv2.database.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.aki.realestatemanagerv2.database.entities.Agent
import com.aki.realestatemanagerv2.database.entities.House

data class AgentWithHouses (
    @Embedded val agent: Agent,
    @Relation(
                parentColumn = "id",
                entityColumn = "agentId"
        )
        val houses: List<House>
)