package com.example.nailsync.domain.model

data class Service(
    val id: Int = 0,
    val name: String,
    val price: Double,
    val durationMinutes: Int,
    val category: String = "NAILS"
)
