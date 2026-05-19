package com.example.nailsync.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "technicians")
data class TechnicianEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val isAvailable: Boolean = true
)
