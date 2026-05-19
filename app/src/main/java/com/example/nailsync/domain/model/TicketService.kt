package com.example.nailsync.domain.model

data class TicketService(
    val id: Int = 0,
    val serviceName: String,
    val technicianId: Int? = null,
    val technicianName: String? = null,
    val price: Double,
    val durationMinutes: Int
)
