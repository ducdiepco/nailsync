package com.example.nailsync.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TicketWithServices(
    @Embedded val ticket: TicketEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "ticketId"
    )
    val services: List<TicketServiceEntity>
)
