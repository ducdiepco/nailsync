package com.example.nailsync.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val durationMinutes: Int,
    val category: String = "NAILS"
)
