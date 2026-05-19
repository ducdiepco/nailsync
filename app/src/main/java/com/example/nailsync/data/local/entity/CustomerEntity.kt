package com.example.nailsync.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val phone: String = "",
    val email: String = "",
    val dateOfBirth: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
