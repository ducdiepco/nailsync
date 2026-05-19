package com.example.nailsync.domain.repository

import com.example.nailsync.domain.model.Technician
import kotlinx.coroutines.flow.Flow

interface TechnicianRepository {
    fun getTechnicians(): Flow<List<Technician>>
    suspend fun updateAvailability(technicianId: Int, isAvailable: Boolean)
}
