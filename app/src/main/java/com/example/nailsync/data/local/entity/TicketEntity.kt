package com.example.nailsync.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerName: String,
    val status: String,
    val tip: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val customerId: Int? = null
)
