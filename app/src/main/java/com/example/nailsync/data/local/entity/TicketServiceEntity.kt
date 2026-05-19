package com.example.nailsync.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ticket_services",
    foreignKeys = [ForeignKey(
        entity = TicketEntity::class,
        parentColumns = ["id"],
        childColumns = ["ticketId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("ticketId")]
)
data class TicketServiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ticketId: Int,
    val catalogServiceId: Int,
    val serviceName: String,
    val technicianId: Int? = null,
    val technicianName: String? = null,
    val price: Double,
    val durationMinutes: Int
)
